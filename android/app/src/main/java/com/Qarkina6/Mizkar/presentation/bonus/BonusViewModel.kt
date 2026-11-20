package com.Qarkina6.Mizkar.presentation.bonus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Qarkina6.Mizkar.core.config.GameConfig
import com.Qarkina6.Mizkar.domain.model.BonusOutcome
import com.Qarkina6.Mizkar.domain.model.BonusRequest
import com.Qarkina6.Mizkar.domain.model.BonusTileKind
import com.Qarkina6.Mizkar.domain.repository.Clock
import com.Qarkina6.Mizkar.domain.repository.RunSessionRepository
import com.Qarkina6.Mizkar.domain.usecase.ResolveBonusUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BonusTileUi(
    val kind: BonusTileKind,
    val credits: Long,
    val revealed: Boolean
)

data class BonusUiState(
    val tiles: List<BonusTileUi> = emptyList(),
    val picksLeft: Int = GameConfig.BONUS_PICKS,
    val total: Long = 0L,
    val steps: Int = 0,
    val finished: Boolean = false,
    val preview: Boolean = false,
    val restored: Boolean = false,
    val bet: Long = GameConfig.BET
)

class BonusViewModel(
    private val session: RunSessionRepository,
    private val resolveBonus: ResolveBonusUseCase,
    private val clock: Clock
) : ViewModel() {

    private val _uiState = MutableStateFlow(BonusUiState())
    val uiState: StateFlow<BonusUiState> = _uiState.asStateFlow()

    private var stopped = false

    init {
        if (session.bonusRequest.value == null) {
            setup(BonusRequest(GameConfig.BET, false, clock.now()), restored = true)
        }
        viewModelScope.launch {
            session.bonusRequest.collect { request ->
                if (request != null) setup(request, restored = false)
            }
        }
    }

    fun pick(index: Int) {
        val state = _uiState.value
        if (state.finished || state.preview) return
        val tile = state.tiles.getOrNull(index) ?: return
        if (tile.revealed) return

        val tiles = state.tiles.toMutableList()
        tiles[index] = tile.copy(revealed = true)
        var total = state.total
        var steps = state.steps
        var finished = false
        when (tile.kind) {
            BonusTileKind.CREDITS -> total += tile.credits
            BonusTileKind.STEP -> steps += 1
            BonusTileKind.STOP -> {
                stopped = true
                finished = true
            }
        }
        val picksLeft = if (finished) 0 else state.picksLeft - 1
        _uiState.value = state.copy(
            tiles = tiles,
            total = total,
            steps = steps,
            picksLeft = picksLeft,
            finished = finished || picksLeft <= 0
        )
    }

    fun collect() {
        val state = _uiState.value
        if (state.preview) {
            session.clearBonusRequest()
            return
        }
        val minimum = resolveBonus.guaranteedMinimum(state.bet)
        session.completeBonus(
            BonusOutcome(
                credits = maxOf(state.total, minimum),
                steps = state.steps,
                stopped = stopped
            )
        )
    }

    private fun setup(request: BonusRequest, restored: Boolean) {
        stopped = false
        val tiles = resolveBonus(request.bet, request.seed).map { tile ->
            BonusTileUi(kind = tile.kind, credits = tile.credits, revealed = request.preview)
        }
        _uiState.value = BonusUiState(
            tiles = tiles,
            picksLeft = if (request.preview) 0 else GameConfig.BONUS_PICKS,
            total = if (request.preview) 0L else 0L,
            steps = 0,
            finished = request.preview || restored,
            preview = request.preview,
            restored = restored,
            bet = request.bet
        )
    }
}
