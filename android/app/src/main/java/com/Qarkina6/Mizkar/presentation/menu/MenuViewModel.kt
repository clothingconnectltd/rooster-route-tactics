package com.Qarkina6.Mizkar.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Qarkina6.Mizkar.core.config.GameConfig
import com.Qarkina6.Mizkar.data.sample.SampleData
import com.Qarkina6.Mizkar.domain.model.AppSettings
import com.Qarkina6.Mizkar.domain.model.PlayerProgress
import com.Qarkina6.Mizkar.domain.repository.CatalogRepository
import com.Qarkina6.Mizkar.domain.repository.Clock
import com.Qarkina6.Mizkar.domain.repository.LeaderboardRepository
import com.Qarkina6.Mizkar.domain.repository.ProgressRepository
import com.Qarkina6.Mizkar.domain.repository.SettingsRepository
import com.Qarkina6.Mizkar.domain.usecase.ClaimDailyRewardUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MenuViewModel(
    private val progressRepository: ProgressRepository,
    private val settingsRepository: SettingsRepository,
    private val leaderboardRepository: LeaderboardRepository,
    private val catalog: CatalogRepository,
    private val claimDailyReward: ClaimDailyRewardUseCase,
    private val clock: Clock
) : ViewModel() {

    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(progressRepository.progress, settingsRepository.settings) { progress, settings ->
                progress to settings
            }.collect { pair -> refresh(pair.first, pair.second) }
        }
        viewModelScope.launch {
            while (isActive) {
                delay(1000L)
                refresh(progressRepository.progress.value, settingsRepository.settings.value)
            }
        }
    }

    fun claimDaily() {
        progressRepository.update { current ->
            claimDailyReward(current, clock.now(), catalog.dailyLadder, SampleData.REWARD_AVATAR)
        }
    }

    fun endBreak() {
        progressRepository.update { current ->
            if (current.breakUntil == 0L) current else current.copy(breakUntil = 0L)
        }
    }

    private fun refresh(progress: PlayerProgress, settings: AppSettings) {
        val now = clock.now()
        val ready = claimDailyReward.isReady(progress, now)
        val day = claimDailyReward.nextDay(progress, now)
        val remaining = GameConfig.DAILY_COOLDOWN_MS - (now - progress.dailyClaimedAt)
        val breakRemaining = progress.breakUntil - now
        _uiState.value = MenuUiState(
            balance = progress.balance,
            dailyDay = day,
            dailyAmount = claimDailyReward.amountFor(day, catalog.dailyLadder),
            dailyReady = ready,
            dailyCountdownSeconds = if (ready) 0L else remaining / 1000L,
            leaderboard = leaderboardRepository.rows(progress.weeklyCredits, settings.leaderboardName),
            breakRemainingSeconds = if (breakRemaining > 0L) breakRemaining / 1000L else 0L,
            reducedMotion = settings.reducedMotion,
            avatarUnlocked = SampleData.REWARD_AVATAR in progress.unlockedRewards
        )
    }
}
