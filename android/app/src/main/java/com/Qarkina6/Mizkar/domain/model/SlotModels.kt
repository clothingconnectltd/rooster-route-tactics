package com.Qarkina6.Mizkar.domain.model

enum class SlotSymbol(val weight: Int, val payThree: Float, val payFour: Float, val payFive: Float) {
    GOLDEN_ROOSTER(7, 4.0f, 12.0f, 50.0f),
    ROUTE_SIGN(5, 2.0f, 4.0f, 8.0f),
    PRIZE_HEN(10, 3.0f, 8.0f, 25.0f),
    GOLDEN_EGG(14, 2.0f, 6.0f, 15.0f),
    FEED_CRATE(18, 1.5f, 5.0f, 12.0f),
    ROAD_BOOT(22, 1.0f, 3.0f, 8.0f),
    FEATHER(24, 0.8f, 2.0f, 6.0f);

    val isWild: Boolean get() = this == GOLDEN_ROOSTER
    val isScatter: Boolean get() = this == ROUTE_SIGN
}

data class LineWin(
    val lineIndex: Int,
    val symbol: SlotSymbol,
    val count: Int,
    val credits: Long
)

data class SpinOutcome(
    val grid: List<List<SlotSymbol>>,
    val lineWins: List<LineWin>,
    val scatterCount: Int,
    val scatterCredits: Long,
    val totalWin: Long,
    val nearWin: Boolean,
    val decidingReel: Int,
    val bonusTriggered: Boolean,
    val wildDropUsed: Boolean,
    val cardDropId: String?
)

data class SpinRequest(
    val seed: Long,
    val lossStreak: Int,
    val bigWinCooldown: Int,
    val wildDropArmed: Boolean,
    val bet: Long
)

data class BonusRequest(
    val bet: Long,
    val preview: Boolean,
    val seed: Long
)

enum class BonusTileKind { CREDITS, STEP, STOP }

data class BonusTile(
    val kind: BonusTileKind,
    val credits: Long
)

data class BonusOutcome(
    val credits: Long,
    val steps: Int,
    val stopped: Boolean
)

data class RunSummary(
    val routeIndex: Int,
    val wagered: Long,
    val won: Long,
    val stepsGained: Int,
    val bestSpin: Long,
    val routeUnlockedIndex: Int,
    val cardDropId: String?
)
