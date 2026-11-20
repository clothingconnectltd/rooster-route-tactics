package com.Qarkina6.Mizkar.presentation.lobby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Qarkina6.Mizkar.domain.model.PlayerProgress
import com.Qarkina6.Mizkar.domain.repository.CatalogRepository
import com.Qarkina6.Mizkar.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LobbyRouteUi(
    val index: Int,
    val nameRes: Int,
    val numeralRes: Int,
    val steps: Int,
    val goal: Int,
    val unlocked: Boolean
)

data class LobbyUiState(
    val routes: List<LobbyRouteUi> = emptyList(),
    val balance: Long = 0L
)

class LobbyViewModel(
    private val progressRepository: ProgressRepository,
    private val catalog: CatalogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LobbyUiState())
    val uiState: StateFlow<LobbyUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            progressRepository.progress.collect { progress -> refresh(progress) }
        }
    }

    private fun refresh(progress: PlayerProgress) {
        _uiState.value = LobbyUiState(
            routes = catalog.routes.map { route ->
                LobbyRouteUi(
                    index = route.index,
                    nameRes = route.nameRes,
                    numeralRes = route.numeralRes,
                    steps = progress.routeSteps.getOrElse(route.index) { 0 },
                    goal = route.stepGoal,
                    unlocked = route.index < progress.unlockedRoutes
                )
            },
            balance = progress.balance
        )
    }
}
