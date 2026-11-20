package com.Qarkina6.Mizkar.data.repository

import com.Qarkina6.Mizkar.data.local.PreferencesStore
import com.Qarkina6.Mizkar.domain.model.AppSettings
import com.Qarkina6.Mizkar.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsRepositoryImpl(
    private val store: PreferencesStore,
    defaultName: String
) : SettingsRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _settings = MutableStateFlow(store.readSettings(defaultName))
    override val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    override fun update(transform: (AppSettings) -> AppSettings) {
        val current = _settings.value
        val next = transform(current)
        if (next == current) return
        _settings.value = next
        scope.launch { store.writeSettings(next) }
    }
}
