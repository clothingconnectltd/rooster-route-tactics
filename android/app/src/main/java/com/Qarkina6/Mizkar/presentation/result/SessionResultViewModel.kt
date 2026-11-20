package com.Qarkina6.Mizkar.presentation.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Qarkina6.Mizkar.R
import com.Qarkina6.Mizkar.domain.model.RunSummary
import com.Qarkina6.Mizkar.domain.repository.CatalogRepository
import com.Qarkina6.Mizkar.domain.repository.ProgressRepository
import com.Qarkina6.Mizkar.domain.repository.RunSessionRepository
import com.Qarkina6.Mizkar.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

enum class ResultTone { WIN, EVEN, LOSS }

data class SessionResultUiState(
    val headlineRes: Int = R.string.result_held,
    val tone: ResultTone = ResultTone.EVEN,
    val won: Long = 0L,
    val steps: Int = 0,
    val bestSpin: Long = 0L,
    val routeNameRes: Int = 0,
    val routeSteps: Int = 0,
    val routeGoal: Int = 10,
    val unlockedRouteNameRes: Int = 0,
    val cardNameRes: Int = 0,
    val cardArtRes: Int = 0,
    val reducedMotion: Boolean = false
)

class SessionResultViewModel(
    private val session: RunSessionRepository,
    private val progressRepository: ProgressRepository,
    private val settingsRepository: SettingsRepository,
    private val catalog: CatalogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SessionResultUiState())
    val uiState: StateFlow<SessionResultUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                session.lastRun,
                progressRepository.progress,
                settingsRepository.settings
            ) { summary, progress, settings ->
                Triple(summary, progress.routeSteps, settings.reducedMotion)
            }.collect { triple ->
                apply(triple.first, triple.second, triple.third)
            }
        }
    }

    private fun apply(summary: RunSummary?, routeSteps: List<Int>, reducedMotion: Boolean) {
        if (summary == null) {
            _uiState.value = SessionResultUiState(reducedMotion = reducedMotion)
            return
        }
        val tone = when {
            summary.won > summary.wagered -> ResultTone.WIN
            summary.won == summary.wagered -> ResultTone.EVEN
            else -> ResultTone.LOSS
        }
        val route = catalog.routes.getOrElse(summary.routeIndex) { catalog.routes.first() }
        val unlocked = catalog.routes.getOrNull(summary.routeUnlockedIndex)
        val card = catalog.cards.firstOrNull { it.id == summary.cardDropId }
        _uiState.value = SessionResultUiState(
            headlineRes = when (tone) {
                ResultTone.WIN -> R.string.result_cleared
                ResultTone.EVEN -> R.string.result_held
                ResultTone.LOSS -> R.string.result_lost
            },
            tone = tone,
            won = summary.won,
            steps = summary.stepsGained,
            bestSpin = summary.bestSpin,
            routeNameRes = route.nameRes,
            routeSteps = routeSteps.getOrElse(summary.routeIndex) { 0 },
            routeGoal = route.stepGoal,
            unlockedRouteNameRes = unlocked?.nameRes ?: 0,
            cardNameRes = card?.nameRes ?: 0,
            cardArtRes = card?.artRes ?: 0,
            reducedMotion = reducedMotion
        )
    }
}
