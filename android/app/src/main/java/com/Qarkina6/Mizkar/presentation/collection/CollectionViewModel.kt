package com.Qarkina6.Mizkar.presentation.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Qarkina6.Mizkar.core.config.GameConfig
import com.Qarkina6.Mizkar.data.sample.SampleData
import com.Qarkina6.Mizkar.domain.model.PlayerProgress
import com.Qarkina6.Mizkar.domain.model.RewardKind
import com.Qarkina6.Mizkar.domain.repository.CatalogRepository
import com.Qarkina6.Mizkar.domain.repository.Clock
import com.Qarkina6.Mizkar.domain.repository.ProgressRepository
import com.Qarkina6.Mizkar.domain.repository.RunSessionRepository
import com.Qarkina6.Mizkar.domain.usecase.ClaimDailyRewardUseCase
import com.Qarkina6.Mizkar.domain.usecase.ClaimMissionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class SymbolCardUi(
    val id: String,
    val nameRes: Int,
    val artRes: Int,
    val setId: String,
    val collected: Boolean
)

data class CardSetUi(
    val id: String,
    val nameRes: Int,
    val collected: Int,
    val total: Int
)

data class MissionUi(
    val id: String,
    val nameRes: Int,
    val iconRes: Int,
    val progress: Int,
    val target: Int,
    val reward: Long,
    val claimed: Boolean
)

data class RewardUi(
    val id: String,
    val nameRes: Int,
    val conditionRes: Int,
    val unlocked: Boolean,
    val equipped: Boolean
)

data class DailyPillUi(
    val day: Int,
    val amount: Long,
    val claimed: Boolean,
    val current: Boolean
)

data class CollectionUiState(
    val tab: Int = 0,
    val balance: Long = 0L,
    val cards: List<SymbolCardUi> = emptyList(),
    val sets: List<CardSetUi> = emptyList(),
    val missions: List<MissionUi> = emptyList(),
    val rewards: List<RewardUi> = emptyList(),
    val dailyPills: List<DailyPillUi> = emptyList(),
    val dailyReady: Boolean = false,
    val dailyAmount: Long = 0L
)

class CollectionViewModel(
    private val progressRepository: ProgressRepository,
    private val catalog: CatalogRepository,
    private val session: RunSessionRepository,
    private val claimMission: ClaimMissionUseCase,
    private val claimDailyReward: ClaimDailyRewardUseCase,
    private val clock: Clock
) : ViewModel() {

    private val _uiState = MutableStateFlow(CollectionUiState())
    val uiState: StateFlow<CollectionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(progressRepository.progress, session.collectionTab) { progress, tab ->
                progress to tab
            }.collect { pair -> refresh(pair.first, pair.second) }
        }
    }

    fun selectTab(tab: Int) {
        session.selectCollectionTab(tab)
    }

    fun claim(missionId: String) {
        val mission = catalog.missions.firstOrNull { it.id == missionId } ?: return
        progressRepository.update { claimMission(it, mission) }
    }

    fun claimDaily() {
        progressRepository.update { current ->
            claimDailyReward(current, clock.now(), catalog.dailyLadder, SampleData.REWARD_AVATAR)
        }
    }

    fun equip(rewardId: String) {
        val reward = catalog.rewards.firstOrNull { it.id == rewardId } ?: return
        progressRepository.update { current ->
            if (reward.id !in current.unlockedRewards) {
                current
            } else {
                when (reward.kind) {
                    RewardKind.BEZEL -> current.copy(equippedBezel = reward.id)
                    RewardKind.TINT -> current.copy(equippedTint = reward.id)
                    RewardKind.AVATAR -> current.copy(equippedAvatar = reward.id)
                }
            }
        }
    }

    private fun refresh(progress: PlayerProgress, tab: Int) {
        val now = clock.now()
        val claimedDay = progress.dailyDay
        val nextDay = claimDailyReward.nextDay(progress, now)
        _uiState.value = CollectionUiState(
            tab = tab,
            balance = progress.balance,
            cards = catalog.cards.map { card ->
                SymbolCardUi(
                    id = card.id,
                    nameRes = card.nameRes,
                    artRes = card.artRes,
                    setId = card.setId,
                    collected = card.id in progress.collectedCards
                )
            },
            sets = catalog.cardSets.map { set ->
                val ids = catalog.cards.filter { it.setId == set.id }
                CardSetUi(
                    id = set.id,
                    nameRes = set.nameRes,
                    collected = ids.count { it.id in progress.collectedCards },
                    total = ids.size
                )
            },
            missions = catalog.missions.map { mission ->
                MissionUi(
                    id = mission.id,
                    nameRes = mission.nameRes,
                    iconRes = mission.iconRes,
                    progress = progress.missionProgress[mission.id] ?: 0,
                    target = mission.target,
                    reward = mission.reward,
                    claimed = mission.id in progress.claimedMissions
                )
            },
            rewards = catalog.rewards.map { reward ->
                val unlocked = reward.id in progress.unlockedRewards ||
                    reward.id == SampleData.REWARD_BRASS_BEZEL
                RewardUi(
                    id = reward.id,
                    nameRes = reward.nameRes,
                    conditionRes = reward.conditionRes,
                    unlocked = unlocked,
                    equipped = when (reward.kind) {
                        RewardKind.BEZEL -> progress.equippedBezel == reward.id
                        RewardKind.TINT -> progress.equippedTint == reward.id
                        RewardKind.AVATAR -> progress.equippedAvatar == reward.id
                    }
                )
            },
            dailyPills = (1..GameConfig.DAILY_DAYS).map { day ->
                DailyPillUi(
                    day = day,
                    amount = claimDailyReward.amountFor(day, catalog.dailyLadder),
                    claimed = day <= claimedDay,
                    current = day == nextDay
                )
            },
            dailyReady = claimDailyReward.isReady(progress, now),
            dailyAmount = claimDailyReward.amountFor(nextDay, catalog.dailyLadder)
        )
    }
}
