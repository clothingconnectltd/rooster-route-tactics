package com.Qarkina6.Mizkar.data.repository

import com.Qarkina6.Mizkar.data.sample.SampleData
import com.Qarkina6.Mizkar.domain.model.BonusOutcome
import com.Qarkina6.Mizkar.domain.model.BonusRequest
import com.Qarkina6.Mizkar.domain.model.CardSet
import com.Qarkina6.Mizkar.domain.model.CollectionCard
import com.Qarkina6.Mizkar.domain.model.LeaderboardEntry
import com.Qarkina6.Mizkar.domain.model.LeaderboardRow
import com.Qarkina6.Mizkar.domain.model.Mission
import com.Qarkina6.Mizkar.domain.model.RouteDefinition
import com.Qarkina6.Mizkar.domain.model.RunSummary
import com.Qarkina6.Mizkar.domain.model.VisualReward
import com.Qarkina6.Mizkar.domain.repository.CatalogRepository
import com.Qarkina6.Mizkar.domain.repository.Clock
import com.Qarkina6.Mizkar.domain.repository.LeaderboardRepository
import com.Qarkina6.Mizkar.domain.repository.RunSessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CatalogRepositoryImpl : CatalogRepository {
    override val routes: List<RouteDefinition> = SampleData.routes
    override val cards: List<CollectionCard> = SampleData.cards
    override val cardSets: List<CardSet> = SampleData.cardSets
    override val missions: List<Mission> = SampleData.missions
    override val rewards: List<VisualReward> = SampleData.rewards
    override val rivals: List<LeaderboardEntry> = SampleData.rivals
    override val dailyLadder: List<Long> = SampleData.dailyLadder
}

class LeaderboardRepositoryImpl(
    private val catalog: CatalogRepository
) : LeaderboardRepository {

    override fun rows(playerCredits: Long, playerName: String): List<LeaderboardRow> {
        val combined = catalog.rivals.map { it to null as String? } +
            listOf(LeaderboardEntry(catalog.rivals.first().nameRes, playerCredits, true) to playerName)
        return combined
            .sortedByDescending { it.first.credits }
            .mapIndexed { index, pair ->
                LeaderboardRow(
                    rank = index + 1,
                    nameRes = pair.first.nameRes,
                    displayName = pair.second,
                    credits = pair.first.credits,
                    isPlayer = pair.first.isPlayer
                )
            }
    }
}

class RunSessionRepositoryImpl(clock: Clock) : RunSessionRepository {

    private val _selectedRoute = MutableStateFlow(0)
    override val selectedRoute: StateFlow<Int> = _selectedRoute.asStateFlow()

    private val _bonusRequest = MutableStateFlow<BonusRequest?>(null)
    override val bonusRequest: StateFlow<BonusRequest?> = _bonusRequest.asStateFlow()

    private val _bonusOutcome = MutableStateFlow<BonusOutcome?>(null)
    override val bonusOutcome: StateFlow<BonusOutcome?> = _bonusOutcome.asStateFlow()

    private val _lastRun = MutableStateFlow<RunSummary?>(null)
    override val lastRun: StateFlow<RunSummary?> = _lastRun.asStateFlow()

    private val _collectionTab = MutableStateFlow(0)
    override val collectionTab: StateFlow<Int> = _collectionTab.asStateFlow()

    override val sessionStartedAt: Long = clock.now()

    override fun selectRoute(index: Int) {
        _selectedRoute.value = index
    }

    override fun requestBonus(request: BonusRequest) {
        _bonusOutcome.value = null
        _bonusRequest.value = request
    }

    override fun clearBonusRequest() {
        _bonusRequest.value = null
    }

    override fun completeBonus(outcome: BonusOutcome) {
        _bonusRequest.value = null
        _bonusOutcome.value = outcome
    }

    override fun consumeBonusOutcome() {
        _bonusOutcome.value = null
    }

    override fun publishRun(summary: RunSummary) {
        _lastRun.value = summary
    }

    override fun selectCollectionTab(tab: Int) {
        _collectionTab.value = tab
    }
}

class SystemClock : Clock {
    override fun now(): Long = System.currentTimeMillis()
}
