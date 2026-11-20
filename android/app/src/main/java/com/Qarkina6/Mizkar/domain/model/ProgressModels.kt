package com.Qarkina6.Mizkar.domain.model

data class RouteDefinition(
    val index: Int,
    val nameRes: Int,
    val numeralRes: Int,
    val stepGoal: Int
)

data class CollectionCard(
    val id: String,
    val nameRes: Int,
    val setId: String,
    val artRes: Int
)

data class CardSet(
    val id: String,
    val nameRes: Int,
    val rewardId: String
)

data class Mission(
    val id: String,
    val nameRes: Int,
    val iconRes: Int,
    val target: Int,
    val reward: Long
)

data class VisualReward(
    val id: String,
    val nameRes: Int,
    val conditionRes: Int,
    val kind: RewardKind
)

enum class RewardKind { BEZEL, TINT, AVATAR }

data class LeaderboardEntry(
    val nameRes: Int,
    val credits: Long,
    val isPlayer: Boolean
)

data class LeaderboardRow(
    val rank: Int,
    val nameRes: Int,
    val displayName: String?,
    val credits: Long,
    val isPlayer: Boolean
)

data class PlayerProgress(
    val balance: Long,
    val routeSteps: List<Int>,
    val unlockedRoutes: Int,
    val collectedCards: Set<String>,
    val missionProgress: Map<String, Int>,
    val claimedMissions: Set<String>,
    val unlockedRewards: Set<String>,
    val equippedBezel: String,
    val equippedTint: String,
    val equippedAvatar: String,
    val dailyDay: Int,
    val dailyClaimedAt: Long,
    val lastRefillAt: Long,
    val weeklyCredits: Long,
    val totalSpins: Int,
    val totalWins: Int,
    val bonusesPlayed: Int,
    val runsFinished: Int,
    val bestSpin: Long,
    val breakUntil: Long
)

data class AppSettings(
    val fastReels: Boolean,
    val nearWinHighlight: Boolean,
    val reducedMotion: Boolean,
    val showRouteMeter: Boolean,
    val leaderboardName: String,
    val reminderMinutes: Int
)

enum class StorageState { OK, FAILED }
