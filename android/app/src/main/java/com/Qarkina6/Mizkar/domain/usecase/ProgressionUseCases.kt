package com.Qarkina6.Mizkar.domain.usecase

import com.Qarkina6.Mizkar.core.config.GameConfig
import com.Qarkina6.Mizkar.domain.model.CardSet
import com.Qarkina6.Mizkar.domain.model.CollectionCard
import com.Qarkina6.Mizkar.domain.model.Mission
import com.Qarkina6.Mizkar.domain.model.PlayerProgress
import com.Qarkina6.Mizkar.domain.model.RouteDefinition
import kotlin.random.Random

object MissionIds {
    const val SPIN_20 = "spin_20"
    const val WIN_5 = "win_5"
    const val BONUS_1 = "bonus_1"
    const val CARDS_2 = "cards_2"
    const val RUN_1 = "run_1"
}

enum class MissionEvent { SPIN, WIN, BONUS, CARD, RUN }

data class StepAward(
    val progress: PlayerProgress,
    val unlockedRouteIndex: Int
)

class AwardRouteStepsUseCase {

    operator fun invoke(
        progress: PlayerProgress,
        routeIndex: Int,
        steps: Int,
        routes: List<RouteDefinition>
    ): StepAward {
        if (steps <= 0 || routeIndex !in routes.indices) return StepAward(progress, -1)
        val goal = routes[routeIndex].stepGoal
        val updated = progress.routeSteps.toMutableList()
        while (updated.size < routes.size) updated.add(0)
        updated[routeIndex] = minOf(updated[routeIndex] + steps, goal)

        var unlocked = progress.unlockedRoutes
        var unlockedIndex = -1
        if (updated[routeIndex] >= goal && unlocked == routeIndex + 1 && unlocked < routes.size) {
            unlocked += 1
            unlockedIndex = routeIndex + 1
        }
        return StepAward(progress.copy(routeSteps = updated, unlockedRoutes = unlocked), unlockedIndex)
    }
}

class RollCollectionDropUseCase {

    operator fun invoke(
        bestCount: Int,
        collected: Set<String>,
        cards: List<CollectionCard>,
        random: Random
    ): String? {
        if (bestCount < GameConfig.CARD_DROP_MIN_COUNT) return null
        if (random.nextFloat() > GameConfig.CARD_DROP_CHANCE) return null
        val remaining = cards.filter { it.id !in collected }
        if (remaining.isEmpty()) return null
        return remaining[random.nextInt(remaining.size)].id
    }
}

class UpdateMissionsUseCase {

    operator fun invoke(
        progress: PlayerProgress,
        event: MissionEvent,
        amount: Int,
        missions: List<Mission>
    ): PlayerProgress {
        val id = when (event) {
            MissionEvent.SPIN -> MissionIds.SPIN_20
            MissionEvent.WIN -> MissionIds.WIN_5
            MissionEvent.BONUS -> MissionIds.BONUS_1
            MissionEvent.CARD -> MissionIds.CARDS_2
            MissionEvent.RUN -> MissionIds.RUN_1
        }
        val mission = missions.firstOrNull { it.id == id } ?: return progress
        if (id in progress.claimedMissions) return progress
        val current = progress.missionProgress[id] ?: 0
        val next = minOf(current + amount, mission.target)
        if (next == current) return progress
        return progress.copy(missionProgress = progress.missionProgress + (id to next))
    }
}

class ClaimMissionUseCase {

    operator fun invoke(progress: PlayerProgress, mission: Mission): PlayerProgress {
        if (mission.id in progress.claimedMissions) return progress
        val done = (progress.missionProgress[mission.id] ?: 0) >= mission.target
        if (!done) return progress
        return progress.copy(
            balance = progress.balance + mission.reward,
            claimedMissions = progress.claimedMissions + mission.id
        )
    }
}

class ClaimDailyRewardUseCase {

    fun isReady(progress: PlayerProgress, now: Long): Boolean =
        now - progress.dailyClaimedAt >= GameConfig.DAILY_COOLDOWN_MS

    fun nextDay(progress: PlayerProgress, now: Long): Int {
        if (progress.dailyClaimedAt == 0L) return 1
        val lapsed = now - progress.dailyClaimedAt >= GameConfig.DAILY_RESET_MS
        if (lapsed) return 1
        return if (progress.dailyDay >= GameConfig.DAILY_DAYS) 1 else progress.dailyDay + 1
    }

    fun amountFor(day: Int, ladder: List<Long>): Long =
        ladder.getOrElse(day - 1) { ladder.lastOrNull() ?: 0L }

    operator fun invoke(progress: PlayerProgress, now: Long, ladder: List<Long>, avatarId: String): PlayerProgress {
        if (!isReady(progress, now)) return progress
        val day = nextDay(progress, now)
        val amount = amountFor(day, ladder)
        val rewards = if (day >= GameConfig.DAILY_DAYS) {
            progress.unlockedRewards + avatarId
        } else {
            progress.unlockedRewards
        }
        return progress.copy(
            balance = progress.balance + amount,
            dailyDay = day,
            dailyClaimedAt = now,
            unlockedRewards = rewards
        )
    }
}

class CompleteSetsUseCase {

    operator fun invoke(
        progress: PlayerProgress,
        cards: List<CollectionCard>,
        sets: List<CardSet>
    ): PlayerProgress {
        var rewards = progress.unlockedRewards
        sets.forEach { set ->
            val ids = cards.filter { it.setId == set.id }.map { it.id }
            if (ids.isNotEmpty() && ids.all { it in progress.collectedCards }) {
                rewards = rewards + set.rewardId
            }
        }
        return if (rewards == progress.unlockedRewards) progress else progress.copy(unlockedRewards = rewards)
    }

    fun completedSets(progress: PlayerProgress, cards: List<CollectionCard>, sets: List<CardSet>): Int =
        sets.count { set ->
            val ids = cards.filter { it.setId == set.id }.map { it.id }
            ids.isNotEmpty() && ids.all { it in progress.collectedCards }
        }
}

class RefillCreditsUseCase {

    fun canRefill(progress: PlayerProgress, now: Long): Boolean =
        progress.balance < GameConfig.BET &&
            now - progress.lastRefillAt >= GameConfig.REFILL_COOLDOWN_MS

    operator fun invoke(progress: PlayerProgress, now: Long): PlayerProgress {
        if (progress.balance >= GameConfig.BET) return progress
        val respectCooldown = now - progress.lastRefillAt >= GameConfig.REFILL_COOLDOWN_MS
        val amount = if (respectCooldown) GameConfig.REFILL_AMOUNT else GameConfig.BET * 4
        return progress.copy(
            balance = progress.balance + amount,
            lastRefillAt = if (respectCooldown) now else progress.lastRefillAt
        )
    }
}
