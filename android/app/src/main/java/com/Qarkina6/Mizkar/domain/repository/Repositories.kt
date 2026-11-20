package com.Qarkina6.Mizkar.domain.repository

import com.Qarkina6.Mizkar.domain.model.AppSettings
import com.Qarkina6.Mizkar.domain.model.BonusOutcome
import com.Qarkina6.Mizkar.domain.model.BonusRequest
import com.Qarkina6.Mizkar.domain.model.CardSet
import com.Qarkina6.Mizkar.domain.model.CollectionCard
import com.Qarkina6.Mizkar.domain.model.LeaderboardEntry
import com.Qarkina6.Mizkar.domain.model.LeaderboardRow
import com.Qarkina6.Mizkar.domain.model.Mission
import com.Qarkina6.Mizkar.domain.model.PlayerProgress
import com.Qarkina6.Mizkar.domain.model.RouteDefinition
import com.Qarkina6.Mizkar.domain.model.RunSummary
import com.Qarkina6.Mizkar.domain.model.StorageState
import com.Qarkina6.Mizkar.domain.model.VisualReward
import kotlinx.coroutines.flow.StateFlow

interface ProgressRepository {
    val progress: StateFlow<PlayerProgress>
    val storageState: StateFlow<StorageState>
    fun update(transform: (PlayerProgress) -> PlayerProgress)
    fun retrySave()
    fun resetAll()
}

interface SettingsRepository {
    val settings: StateFlow<AppSettings>
    fun update(transform: (AppSettings) -> AppSettings)
}

interface CatalogRepository {
    val routes: List<RouteDefinition>
    val cards: List<CollectionCard>
    val cardSets: List<CardSet>
    val missions: List<Mission>
    val rewards: List<VisualReward>
    val rivals: List<LeaderboardEntry>
    val dailyLadder: List<Long>
}

interface LeaderboardRepository {
    fun rows(playerCredits: Long, playerName: String): List<LeaderboardRow>
}

interface RunSessionRepository {
    val selectedRoute: StateFlow<Int>
    val bonusRequest: StateFlow<BonusRequest?>
    val bonusOutcome: StateFlow<BonusOutcome?>
    val lastRun: StateFlow<RunSummary?>
    val collectionTab: StateFlow<Int>
    val sessionStartedAt: Long
    fun selectRoute(index: Int)
    fun requestBonus(request: BonusRequest)
    fun clearBonusRequest()
    fun completeBonus(outcome: BonusOutcome)
    fun consumeBonusOutcome()
    fun publishRun(summary: RunSummary)
    fun selectCollectionTab(tab: Int)
}

interface Clock {
    fun now(): Long
}
