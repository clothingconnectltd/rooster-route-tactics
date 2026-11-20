package com.Qarkina6.Mizkar.presentation.responsible

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Qarkina6.Mizkar.core.config.GameConfig
import com.Qarkina6.Mizkar.domain.repository.Clock
import com.Qarkina6.Mizkar.domain.repository.ProgressRepository
import com.Qarkina6.Mizkar.domain.repository.RunSessionRepository
import com.Qarkina6.Mizkar.domain.repository.SettingsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class ResponsiblePlayUiState(
    val sessionSeconds: Long = 0L,
    val spinsTotal: Int = 0,
    val reminderMinutes: Int = GameConfig.SESSION_REMINDER_SHORT_MIN,
    val showResetDialog: Boolean = false,
    val showReminder: Boolean = false
)

class ResponsiblePlayViewModel(
    private val progressRepository: ProgressRepository,
    private val settingsRepository: SettingsRepository,
    private val session: RunSessionRepository,
    private val clock: Clock
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResponsiblePlayUiState())
    val uiState: StateFlow<ResponsiblePlayUiState> = _uiState.asStateFlow()

    private var reminderShown = false

    init {
        viewModelScope.launch {
            while (isActive) {
                tick()
                delay(1000L)
            }
        }
    }

    fun setReminderMinutes(minutes: Int) {
        reminderShown = false
        settingsRepository.update { it.copy(reminderMinutes = minutes) }
    }

    fun dismissReminder() {
        _uiState.value = _uiState.value.copy(showReminder = false)
    }

    fun askReset() {
        _uiState.value = _uiState.value.copy(showResetDialog = true)
    }

    fun cancelReset() {
        _uiState.value = _uiState.value.copy(showResetDialog = false)
    }

    fun confirmReset() {
        _uiState.value = _uiState.value.copy(showResetDialog = false)
        progressRepository.resetAll()
    }

    fun takeBreak() {
        val until = clock.now() + GameConfig.BREAK_DURATION_MS
        progressRepository.update { it.copy(breakUntil = until) }
    }

    private fun tick() {
        val elapsed = (clock.now() - session.sessionStartedAt) / 1000L
        val settings = settingsRepository.settings.value
        val threshold = settings.reminderMinutes * 60L
        val due = elapsed >= threshold && !reminderShown
        if (due) reminderShown = true
        _uiState.value = _uiState.value.copy(
            sessionSeconds = elapsed,
            spinsTotal = progressRepository.progress.value.totalSpins,
            reminderMinutes = settings.reminderMinutes,
            showReminder = _uiState.value.showReminder || due
        )
    }
}
