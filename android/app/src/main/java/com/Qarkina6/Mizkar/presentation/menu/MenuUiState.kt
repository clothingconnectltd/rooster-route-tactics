package com.Qarkina6.Mizkar.presentation.menu

import com.Qarkina6.Mizkar.domain.model.LeaderboardRow

data class MenuUiState(
    val balance: Long = 0L,
    val dailyDay: Int = 1,
    val dailyAmount: Long = 0L,
    val dailyReady: Boolean = false,
    val dailyCountdownSeconds: Long = 0L,
    val leaderboard: List<LeaderboardRow> = emptyList(),
    val breakRemainingSeconds: Long = 0L,
    val reducedMotion: Boolean = false,
    val avatarUnlocked: Boolean = false
)
