package com.Qarkina6.Mizkar.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Qarkina6.Mizkar.core.config.GameConfig
import com.Qarkina6.Mizkar.domain.model.AppSettings
import com.Qarkina6.Mizkar.domain.model.BonusOutcome
import com.Qarkina6.Mizkar.domain.model.BonusRequest
import com.Qarkina6.Mizkar.domain.model.PlayerProgress
import com.Qarkina6.Mizkar.domain.model.RunSummary
import com.Qarkina6.Mizkar.domain.model.SlotSymbol
import com.Qarkina6.Mizkar.domain.model.SpinOutcome
import com.Qarkina6.Mizkar.domain.model.SpinRequest
import com.Qarkina6.Mizkar.domain.repository.CatalogRepository
import com.Qarkina6.Mizkar.domain.repository.Clock
import com.Qarkina6.Mizkar.domain.repository.ProgressRepository
import com.Qarkina6.Mizkar.domain.repository.RunSessionRepository
import com.Qarkina6.Mizkar.domain.repository.SettingsRepository
import com.Qarkina6.Mizkar.domain.usecase.AwardRouteStepsUseCase
import com.Qarkina6.Mizkar.domain.usecase.CompleteSetsUseCase
import com.Qarkina6.Mizkar.domain.usecase.MissionEvent
import com.Qarkina6.Mizkar.domain.usecase.PAY_LINES
import com.Qarkina6.Mizkar.domain.usecase.RefillCreditsUseCase
import com.Qarkina6.Mizkar.domain.usecase.RollCollectionDropUseCase
import com.Qarkina6.Mizkar.domain.usecase.SpinReelsUseCase
import com.Qarkina6.Mizkar.domain.usecase.UpdateMissionsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel(
    private val progressRepository: ProgressRepository,
    private val settingsRepository: SettingsRepository,
    private val catalog: CatalogRepository,
    private val session: RunSessionRepository,
    private val spinReels: SpinReelsUseCase,
    private val awardRouteSteps: AwardRouteStepsUseCase,
    private val rollCollectionDrop: RollCollectionDropUseCase,
    private val updateMissions: UpdateMissionsUseCase,
    private val completeSets: CompleteSetsUseCase,
    private val refillCredits: RefillCreditsUseCase,
    private val clock: Clock
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val eventChannel = Channel<GameEvent>(Channel.BUFFERED)
    val events: Flow<GameEvent> = eventChannel.receiveAsFlow()

    private var spinJob: Job? = null
    private var lossStreak = 0
    private var bigWinCooldown = 0
    private var runIndex = 0
    private var spinsResolved = 0
    private var runWon = 0L
    private var runWagered = 0L
    private var runSteps = 0
    private var runBest = 0L
    private var runUnlocked = -1
    private var runCard: String? = null
    private var bonusPending = false
    private val blurRandom = Random(clock.now())

    init {
        viewModelScope.launch {
            combine(
                progressRepository.progress,
                settingsRepository.settings,
                session.selectedRoute
            ) { progress, settings, route -> Triple(progress, settings, route) }
                .collect { triple -> applyExternalState(triple.first, triple.second, triple.third) }
        }
        viewModelScope.launch {
            session.bonusOutcome.collect { outcome -> if (outcome != null) applyBonusOutcome(outcome) }
        }
        startRun()
    }

    fun spin() {
        val state = _uiState.value
        if (state.isSpinning) return
        val progress = progressRepository.progress.value
        if (progress.balance < GameConfig.BET) {
            _uiState.value = state.copy(lowCredits = true)
            return
        }
        runWagered += GameConfig.BET
        progressRepository.update { it.copy(balance = it.balance - GameConfig.BET) }
        _uiState.value = state.copy(
            isSpinning = true,
            showHint = false,
            showWinBanner = false,
            showNoPay = false,
            winningCells = emptySet(),
            winningLines = emptyList(),
            nearWinReel = -1
        )
        launchSpin()
    }

    fun refill() {
        val now = clock.now()
        progressRepository.update { refillCredits(it, now) }
        _uiState.value = _uiState.value.copy(lowCredits = false)
    }

    fun openPause() {
        _uiState.value = _uiState.value.copy(showPause = true)
    }

    fun closePause() {
        _uiState.value = _uiState.value.copy(showPause = false)
    }

    fun openHowTo() {
        _uiState.value = _uiState.value.copy(showHowTo = true)
    }

    fun closeHowTo() {
        _uiState.value = _uiState.value.copy(showHowTo = false)
    }

    fun endRunEarly() {
        _uiState.value = _uiState.value.copy(showPause = false)
        spinJob?.cancel()
        spinJob = null
        _uiState.value = _uiState.value.copy(isSpinning = false)
        finishRun()
    }

    fun pause() {
        spinJob?.cancel()
        spinJob = null
        if (_uiState.value.isSpinning) {
            _uiState.value = _uiState.value.copy(isSpinning = false)
        }
    }

    private fun launchSpin() {
        val settings = settingsRepository.settings.value
        val routeIndex = session.selectedRoute.value
        val seed = seedFor(routeIndex, runIndex, spinsResolved)
        val armed = _uiState.value.wildDropArmed
        val outcome = spinReels(
            SpinRequest(
                seed = seed,
                lossStreak = lossStreak,
                bigWinCooldown = bigWinCooldown,
                wildDropArmed = armed,
                bet = GameConfig.BET
            )
        )
        var settled = false
        spinJob = viewModelScope.launch {
            try {
                animateReels(outcome, settings)
            } finally {
                if (!settled) {
                    settled = true
                    settle(outcome, settings)
                }
            }
            if (!outcome.bonusTriggered) {
                delay(GameConfig.RESULT_HOLD_MS)
                if (spinsResolved >= GameConfig.SPINS_PER_RUN) finishRun()
            }
        }
    }

    private suspend fun animateReels(outcome: SpinOutcome, settings: AppSettings) {
        val duration = if (settings.fastReels) {
            GameConfig.SPIN_DURATION_FAST_MS
        } else {
            GameConfig.SPIN_DURATION_NORMAL_MS
        }
        val stagger = if (settings.fastReels) {
            GameConfig.REEL_STAGGER_FAST_MS
        } else {
            GameConfig.REEL_STAGGER_NORMAL_MS
        }
        val extra = if (settings.nearWinHighlight && outcome.nearWin) GameConfig.NEAR_WIN_EXTRA_MS else 0L
        val stops = List(GameConfig.REELS) { reel ->
            duration + reel * stagger + if (reel == outcome.decidingReel) extra else 0L
        }
        val working = MutableList(GameConfig.ROWS) { row ->
            MutableList(GameConfig.REELS) { reel -> outcome.grid[row][reel] }
        }
        val pool = SlotSymbol.entries
        var elapsed = 0L
        while (true) {
            val spinning = List(GameConfig.REELS) { elapsed < stops[it] }
            if (spinning.none { it }) break
            for (reel in 0 until GameConfig.REELS) {
                for (row in 0 until GameConfig.ROWS) {
                    working[row][reel] = if (spinning[reel]) {
                        pool[blurRandom.nextInt(pool.size)]
                    } else {
                        outcome.grid[row][reel]
                    }
                }
            }
            val snapshot = working.map { it.toList() }
            _uiState.value = _uiState.value.copy(grid = snapshot, spinningReels = spinning)
            delay(GameConfig.REEL_TICK_MS)
            elapsed += GameConfig.REEL_TICK_MS
        }
        _uiState.value = _uiState.value.copy(
            grid = outcome.grid,
            spinningReels = List(GameConfig.REELS) { false }
        )
    }

    private fun settle(outcome: SpinOutcome, settings: AppSettings) {
        spinsResolved += 1
        val won = outcome.totalWin > 0L
        val bestCount = outcome.lineWins.maxOfOrNull { it.count } ?: 0
        val routeIndex = session.selectedRoute.value
        val current = progressRepository.progress.value
        val dropRandom = Random(seedFor(routeIndex, runIndex, spinsResolved) + 17L)
        val card = rollCollectionDrop(bestCount, current.collectedCards, catalog.cards, dropRandom)
        val setBoost = completeSets.completedSets(current, catalog.cards, catalog.cardSets)

        var next = current.copy(
            balance = current.balance + outcome.totalWin,
            totalSpins = current.totalSpins + 1,
            totalWins = if (won) current.totalWins + 1 else current.totalWins,
            bestSpin = maxOf(current.bestSpin, outcome.totalWin),
            weeklyCredits = current.weeklyCredits + outcome.totalWin
        )
        next = updateMissions(next, MissionEvent.SPIN, 1, catalog.missions)
        if (won) next = updateMissions(next, MissionEvent.WIN, 1, catalog.missions)
        var stepsGained = 0
        if (won) {
            val award = awardRouteSteps(next, routeIndex, 1 + setBoost, catalog.routes)
            next = award.progress
            stepsGained = 1 + setBoost
            if (award.unlockedRouteIndex >= 0) runUnlocked = award.unlockedRouteIndex
        }
        if (card != null) {
            next = next.copy(collectedCards = next.collectedCards + card)
            next = updateMissions(next, MissionEvent.CARD, 1, catalog.missions)
            next = completeSets(next, catalog.cards, catalog.cardSets)
            runCard = card
        }
        progressRepository.update { next }

        runWon += outcome.totalWin
        runSteps += stepsGained
        runBest = maxOf(runBest, outcome.totalWin)

        lossStreak = if (won) 0 else lossStreak + 1
        bigWinCooldown = when {
            outcome.totalWin >= GameConfig.BET * GameConfig.BIG_WIN_MULTIPLIER -> GameConfig.BIG_WIN_COOLDOWN_SPINS
            bigWinCooldown > 0 -> bigWinCooldown - 1
            else -> 0
        }

        var charge = _uiState.value.featherCharge
        if (outcome.wildDropUsed) charge = 0
        if (outcome.nearWin) charge = minOf(charge + 1, GameConfig.FEATHER_MAX)

        val cells = mutableSetOf<Int>()
        outcome.lineWins.forEach { win ->
            val pattern = PAY_LINES[win.lineIndex]
            for (reel in 0 until win.count) {
                cells.add(pattern[reel] * GameConfig.REELS + reel)
            }
        }

        _uiState.value = _uiState.value.copy(
            isSpinning = false,
            roundIndex = minOf(spinsResolved + 1, GameConfig.SPINS_PER_RUN),
            winningCells = cells,
            winningLines = outcome.lineWins.map { it.lineIndex },
            lastWin = outcome.totalWin,
            winSymbolRes = outcome.lineWins.maxByOrNull { it.credits }?.let { nameOf(it.symbol) } ?: 0,
            showWinBanner = won,
            showNoPay = !won,
            nearWinReel = if (settings.nearWinHighlight && outcome.nearWin) outcome.decidingReel else -1,
            featherCharge = charge,
            wildDropArmed = charge >= GameConfig.FEATHER_MAX
        )

        if (outcome.bonusTriggered) {
            bonusPending = true
            session.requestBonus(
                BonusRequest(
                    bet = GameConfig.BET,
                    preview = false,
                    seed = seedFor(routeIndex, runIndex, spinsResolved) + 101L
                )
            )
            eventChannel.trySend(GameEvent.BonusTriggered)
        }
    }

    private fun applyBonusOutcome(outcome: BonusOutcome) {
        session.consumeBonusOutcome()
        if (!bonusPending) return
        bonusPending = false
        val routeIndex = session.selectedRoute.value
        val current = progressRepository.progress.value
        var next = current.copy(
            balance = current.balance + outcome.credits,
            weeklyCredits = current.weeklyCredits + outcome.credits,
            bonusesPlayed = current.bonusesPlayed + 1
        )
        next = updateMissions(next, MissionEvent.BONUS, 1, catalog.missions)
        if (outcome.steps > 0) {
            val award = awardRouteSteps(next, routeIndex, outcome.steps, catalog.routes)
            next = award.progress
            if (award.unlockedRouteIndex >= 0) runUnlocked = award.unlockedRouteIndex
        }
        progressRepository.update { next }
        runWon += outcome.credits
        runSteps += outcome.steps
        if (spinsResolved >= GameConfig.SPINS_PER_RUN) {
            finishRun()
        }
    }

    private fun finishRun() {
        val routeIndex = session.selectedRoute.value
        val summary = RunSummary(
            routeIndex = routeIndex,
            wagered = runWagered,
            won = runWon,
            stepsGained = runSteps,
            bestSpin = runBest,
            routeUnlockedIndex = runUnlocked,
            cardDropId = runCard
        )
        session.publishRun(summary)
        progressRepository.update { current ->
            updateMissions(
                current.copy(runsFinished = current.runsFinished + 1),
                MissionEvent.RUN,
                1,
                catalog.missions
            )
        }
        eventChannel.trySend(GameEvent.RunFinished)
        startRun()
    }

    private fun startRun() {
        runIndex += 1
        spinsResolved = 0
        runWon = 0L
        runWagered = 0L
        runSteps = 0
        runBest = 0L
        runUnlocked = -1
        runCard = null
        bonusPending = false
        _uiState.value = _uiState.value.copy(
            roundIndex = 1,
            isSpinning = false,
            showHint = true,
            showWinBanner = false,
            showNoPay = false,
            winningCells = emptySet(),
            winningLines = emptyList(),
            lastWin = 0L,
            nearWinReel = -1
        )
    }

    private fun applyExternalState(progress: PlayerProgress, settings: AppSettings, routeIndex: Int) {
        val route = catalog.routes.getOrElse(routeIndex) { catalog.routes.first() }
        _uiState.value = _uiState.value.copy(
            routeNameRes = route.nameRes,
            balance = progress.balance,
            routeSteps = progress.routeSteps.getOrElse(routeIndex) { 0 },
            routeGoal = route.stepGoal,
            lowCredits = progress.balance < GameConfig.BET,
            showRouteMeter = settings.showRouteMeter,
            reducedMotion = settings.reducedMotion,
            bezelId = progress.equippedBezel,
            tintId = progress.equippedTint
        )
    }

    private fun seedFor(routeIndex: Int, run: Int, spin: Int): Long =
        (routeIndex + 1) * GameConfig.SEED_ROUTE_FACTOR xor
            (run * GameConfig.SEED_RUN_FACTOR + spin * 31L)
}
