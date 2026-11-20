package com.Qarkina6.Mizkar.data.local

import android.content.Context
import android.content.SharedPreferences
import com.Qarkina6.Mizkar.core.config.GameConfig
import com.Qarkina6.Mizkar.domain.model.AppSettings
import com.Qarkina6.Mizkar.domain.model.PlayerProgress

class PreferencesStore(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE)

    fun readProgress(routeCount: Int): PlayerProgress = PlayerProgress(
        balance = prefs.getLong(KEY_BALANCE, GameConfig.START_BALANCE),
        routeSteps = decodeInts(prefs.getString(KEY_STEPS, null), routeCount),
        unlockedRoutes = prefs.getInt(KEY_UNLOCKED, 1).coerceIn(1, routeCount),
        collectedCards = decodeSet(prefs.getString(KEY_CARDS, null)),
        missionProgress = decodeCounts(prefs.getString(KEY_MISSIONS, null)),
        claimedMissions = decodeSet(prefs.getString(KEY_MISSIONS_DONE, null)),
        unlockedRewards = decodeSet(prefs.getString(KEY_REWARDS, null)),
        equippedBezel = prefs.getString(KEY_BEZEL, "") ?: "",
        equippedTint = prefs.getString(KEY_TINT, "") ?: "",
        equippedAvatar = prefs.getString(KEY_AVATAR, "") ?: "",
        dailyDay = prefs.getInt(KEY_DAILY_DAY, 0),
        dailyClaimedAt = prefs.getLong(KEY_DAILY_AT, 0L),
        lastRefillAt = prefs.getLong(KEY_REFILL_AT, 0L),
        weeklyCredits = prefs.getLong(KEY_WEEKLY, 0L),
        totalSpins = prefs.getInt(KEY_SPINS, 0),
        totalWins = prefs.getInt(KEY_WINS, 0),
        bonusesPlayed = prefs.getInt(KEY_BONUSES, 0),
        runsFinished = prefs.getInt(KEY_RUNS, 0),
        bestSpin = prefs.getLong(KEY_BEST, 0L),
        breakUntil = prefs.getLong(KEY_BREAK, 0L)
    )

    fun writeProgress(progress: PlayerProgress): Boolean = runCatching {
        prefs.edit()
            .putLong(KEY_BALANCE, progress.balance)
            .putString(KEY_STEPS, progress.routeSteps.joinToString(SEPARATOR))
            .putInt(KEY_UNLOCKED, progress.unlockedRoutes)
            .putString(KEY_CARDS, progress.collectedCards.joinToString(SEPARATOR))
            .putString(KEY_MISSIONS, encodeCounts(progress.missionProgress))
            .putString(KEY_MISSIONS_DONE, progress.claimedMissions.joinToString(SEPARATOR))
            .putString(KEY_REWARDS, progress.unlockedRewards.joinToString(SEPARATOR))
            .putString(KEY_BEZEL, progress.equippedBezel)
            .putString(KEY_TINT, progress.equippedTint)
            .putString(KEY_AVATAR, progress.equippedAvatar)
            .putInt(KEY_DAILY_DAY, progress.dailyDay)
            .putLong(KEY_DAILY_AT, progress.dailyClaimedAt)
            .putLong(KEY_REFILL_AT, progress.lastRefillAt)
            .putLong(KEY_WEEKLY, progress.weeklyCredits)
            .putInt(KEY_SPINS, progress.totalSpins)
            .putInt(KEY_WINS, progress.totalWins)
            .putInt(KEY_BONUSES, progress.bonusesPlayed)
            .putInt(KEY_RUNS, progress.runsFinished)
            .putLong(KEY_BEST, progress.bestSpin)
            .putLong(KEY_BREAK, progress.breakUntil)
            .commit()
    }.getOrDefault(false)

    fun readSettings(defaultName: String): AppSettings = AppSettings(
        fastReels = prefs.getBoolean(KEY_FAST, false),
        nearWinHighlight = prefs.getBoolean(KEY_NEAR_WIN, true),
        reducedMotion = prefs.getBoolean(KEY_REDUCED, false),
        showRouteMeter = prefs.getBoolean(KEY_ROUTE_METER, true),
        leaderboardName = prefs.getString(KEY_NAME, defaultName) ?: defaultName,
        reminderMinutes = prefs.getInt(KEY_REMINDER, GameConfig.SESSION_REMINDER_SHORT_MIN)
    )

    fun writeSettings(settings: AppSettings): Boolean = runCatching {
        prefs.edit()
            .putBoolean(KEY_FAST, settings.fastReels)
            .putBoolean(KEY_NEAR_WIN, settings.nearWinHighlight)
            .putBoolean(KEY_REDUCED, settings.reducedMotion)
            .putBoolean(KEY_ROUTE_METER, settings.showRouteMeter)
            .putString(KEY_NAME, settings.leaderboardName)
            .putInt(KEY_REMINDER, settings.reminderMinutes)
            .commit()
    }.getOrDefault(false)

    fun clearProgress(): Boolean = runCatching {
        prefs.edit()
            .remove(KEY_BALANCE)
            .remove(KEY_STEPS)
            .remove(KEY_UNLOCKED)
            .remove(KEY_CARDS)
            .remove(KEY_MISSIONS)
            .remove(KEY_MISSIONS_DONE)
            .remove(KEY_REWARDS)
            .remove(KEY_BEZEL)
            .remove(KEY_TINT)
            .remove(KEY_AVATAR)
            .remove(KEY_DAILY_DAY)
            .remove(KEY_DAILY_AT)
            .remove(KEY_REFILL_AT)
            .remove(KEY_WEEKLY)
            .remove(KEY_SPINS)
            .remove(KEY_WINS)
            .remove(KEY_BONUSES)
            .remove(KEY_RUNS)
            .remove(KEY_BEST)
            .remove(KEY_BREAK)
            .commit()
    }.getOrDefault(false)

    private fun decodeInts(raw: String?, size: Int): List<Int> {
        val parsed = raw?.split(SEPARATOR)?.mapNotNull { it.trim().toIntOrNull() } ?: emptyList()
        return List(size) { parsed.getOrElse(it) { 0 } }
    }

    private fun decodeSet(raw: String?): Set<String> =
        raw?.split(SEPARATOR)?.filter { it.isNotBlank() }?.toSet() ?: emptySet()

    private fun decodeCounts(raw: String?): Map<String, Int> {
        if (raw.isNullOrBlank()) return emptyMap()
        return raw.split(SEPARATOR).mapNotNull { entry ->
            val parts = entry.split(PAIR_SEPARATOR)
            val value = parts.getOrNull(1)?.toIntOrNull()
            if (parts.size == 2 && value != null) parts[0] to value else null
        }.toMap()
    }

    private fun encodeCounts(counts: Map<String, Int>): String =
        counts.entries.joinToString(SEPARATOR) { "${it.key}$PAIR_SEPARATOR${it.value}" }

    private companion object {
        const val STORE_NAME = "rooster_route_store"
        const val SEPARATOR = "|"
        const val PAIR_SEPARATOR = ":"
        const val KEY_BALANCE = "balance"
        const val KEY_STEPS = "route_steps"
        const val KEY_UNLOCKED = "unlocked_routes"
        const val KEY_CARDS = "cards"
        const val KEY_MISSIONS = "mission_progress"
        const val KEY_MISSIONS_DONE = "mission_claimed"
        const val KEY_REWARDS = "rewards"
        const val KEY_BEZEL = "bezel"
        const val KEY_TINT = "tint"
        const val KEY_AVATAR = "avatar"
        const val KEY_DAILY_DAY = "daily_day"
        const val KEY_DAILY_AT = "daily_at"
        const val KEY_REFILL_AT = "refill_at"
        const val KEY_WEEKLY = "weekly"
        const val KEY_SPINS = "spins"
        const val KEY_WINS = "wins"
        const val KEY_BONUSES = "bonuses"
        const val KEY_RUNS = "runs"
        const val KEY_BEST = "best"
        const val KEY_BREAK = "break_until"
        const val KEY_FAST = "fast_reels"
        const val KEY_NEAR_WIN = "near_win"
        const val KEY_REDUCED = "reduced_motion"
        const val KEY_ROUTE_METER = "route_meter"
        const val KEY_NAME = "player_name"
        const val KEY_REMINDER = "reminder_minutes"
    }
}
