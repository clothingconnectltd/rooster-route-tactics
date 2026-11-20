package com.Qarkina6.Mizkar.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Qarkina6.Mizkar.domain.model.StorageState
import com.Qarkina6.Mizkar.domain.repository.ProgressRepository
import com.Qarkina6.Mizkar.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class SettingsUiState(
    val fastReels: Boolean = false,
    val nearWinHighlight: Boolean = true,
    val reducedMotion: Boolean = false,
    val showRouteMeter: Boolean = true,
    val leaderboardName: String = "",
    val storageOk: Boolean = true
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                settingsRepository.settings,
                progressRepository.storageState
            ) { settings, storage ->
                SettingsUiState(
                    fastReels = settings.fastReels,
                    nearWinHighlight = settings.nearWinHighlight,
                    reducedMotion = settings.reducedMotion,
                    showRouteMeter = settings.showRouteMeter,
                    leaderboardName = settings.leaderboardName,
                    storageOk = storage == StorageState.OK
                )
            }.collect { state -> _uiState.value = state }
        }
    }

    fun setFastReels(value: Boolean) {
        settingsRepository.update { it.copy(fastReels = value) }
    }

    fun setNearWinHighlight(value: Boolean) {
        settingsRepository.update { it.copy(nearWinHighlight = value) }
    }

    fun setReducedMotion(value: Boolean) {
        settingsRepository.update { it.copy(reducedMotion = value) }
    }

    fun setShowRouteMeter(value: Boolean) {
        settingsRepository.update { it.copy(showRouteMeter = value) }
    }

    fun setLeaderboardName(value: String) {
        val trimmed = value.take(12)
        settingsRepository.update { it.copy(leaderboardName = trimmed) }
    }

    fun retrySave() {
        progressRepository.retrySave()
    }
}
