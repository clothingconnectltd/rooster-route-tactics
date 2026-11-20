package com.Qarkina6.Mizkar.core.config

object GameConfig {
    const val LOADER_DURATION_MS = 8000L

    const val REELS = 5
    const val ROWS = 3
    const val PAY_LINES = 5

    const val BET = 50L
    const val START_BALANCE = 5000L
    const val SPINS_PER_RUN = 5

    const val REFILL_AMOUNT = 1000L
    const val REFILL_COOLDOWN_MS = 3_600_000L

    const val SPIN_DURATION_NORMAL_MS = 1900L
    const val SPIN_DURATION_FAST_MS = 1200L
    const val REEL_STAGGER_NORMAL_MS = 150L
    const val REEL_STAGGER_FAST_MS = 100L
    const val NEAR_WIN_EXTRA_MS = 400L
    const val REEL_TICK_MS = 70L
    const val RESULT_HOLD_MS = 600L

    const val FEATHER_MAX = 3
    const val PITY_LOSS_STREAK = 5
    const val PITY_WILD_FACTOR = 1.4f
    const val BIG_WIN_MULTIPLIER = 20L
    const val BIG_WIN_COOLDOWN_SPINS = 2

    const val BONUS_PICKS = 3
    const val BONUS_TILES = 6
    const val BONUS_MIN_MULTIPLIER = 2L

    const val CARD_DROP_CHANCE = 0.20f
    const val CARD_DROP_MIN_COUNT = 4

    const val DAILY_COOLDOWN_MS = 72_000_000L
    const val DAILY_RESET_MS = 172_800_000L
    const val DAILY_DAYS = 7

    const val BREAK_DURATION_MS = 300_000L
    const val SESSION_REMINDER_SHORT_MIN = 30
    const val SESSION_REMINDER_LONG_MIN = 60

    const val SEED_ROUTE_FACTOR = 7919L
    const val SEED_RUN_FACTOR = 104729L
}
