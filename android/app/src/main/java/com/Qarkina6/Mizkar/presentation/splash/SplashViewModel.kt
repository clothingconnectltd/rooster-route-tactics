package com.Qarkina6.Mizkar.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Qarkina6.Mizkar.core.config.GameConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SplashUiState(
    val finished: Boolean = false,
    val reducedMotion: Boolean = false
)

class SplashViewModel(reducedMotion: Boolean) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState(reducedMotion = reducedMotion))
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            delay(GameConfig.LOADER_DURATION_MS)
            _uiState.update { it.copy(finished = true) }
        }
    }
}
