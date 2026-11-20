package com.Qarkina6.Mizkar.presentation.game

import com.Qarkina6.Mizkar.core.config.GameConfig
import com.Qarkina6.Mizkar.domain.model.SlotSymbol

data class GameUiState(
    val routeNameRes: Int = 0,
    val roundIndex: Int = 1,
    val balance: Long = 0L,
    val grid: List<List<SlotSymbol>> = idleGrid(),
    val spinningReels: List<Boolean> = List(GameConfig.REELS) { false },
    val winningCells: Set<Int> = emptySet(),
    val winningLines: List<Int> = emptyList(),
    val lastWin: Long = 0L,
    val winSymbolRes: Int = 0,
    val showWinBanner: Boolean = false,
    val showNoPay: Boolean = false,
    val nearWinReel: Int = -1,
    val featherCharge: Int = 0,
    val wildDropArmed: Boolean = false,
    val routeSteps: Int = 0,
    val routeGoal: Int = 10,
    val isSpinning: Boolean = false,
    val showHint: Boolean = true,
    val lowCredits: Boolean = false,
    val showRouteMeter: Boolean = true,
    val reducedMotion: Boolean = false,
    val bezelId: String = "",
    val tintId: String = "",
    val showPause: Boolean = false,
    val showHowTo: Boolean = false
)

fun idleGrid(): List<List<SlotSymbol>> {
    val pool = SlotSymbol.entries
    return List(GameConfig.ROWS) { row ->
        List(GameConfig.REELS) { reel -> pool[(row * 3 + reel * 2 + 2) % pool.size] }
    }
}

sealed interface GameEvent {
    data object BonusTriggered : GameEvent
    data object RunFinished : GameEvent
}
