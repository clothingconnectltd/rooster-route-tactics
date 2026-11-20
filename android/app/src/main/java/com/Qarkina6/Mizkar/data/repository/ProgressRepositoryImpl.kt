package com.Qarkina6.Mizkar.data.repository

import com.Qarkina6.Mizkar.data.local.PreferencesStore
import com.Qarkina6.Mizkar.domain.model.PlayerProgress
import com.Qarkina6.Mizkar.domain.model.StorageState
import com.Qarkina6.Mizkar.domain.repository.ProgressRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProgressRepositoryImpl(
    private val store: PreferencesStore,
    private val routeCount: Int
) : ProgressRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _progress = MutableStateFlow(store.readProgress(routeCount))
    override val progress: StateFlow<PlayerProgress> = _progress.asStateFlow()

    private val _storageState = MutableStateFlow(StorageState.OK)
    override val storageState: StateFlow<StorageState> = _storageState.asStateFlow()

    override fun update(transform: (PlayerProgress) -> PlayerProgress) {
        val current = _progress.value
        val next = transform(current)
        if (next == current) return
        _progress.value = next
        persist(next)
    }

    override fun retrySave() {
        persist(_progress.value)
    }

    override fun resetAll() {
        scope.launch {
            val cleared = store.clearProgress()
            _progress.value = store.readProgress(routeCount)
            _storageState.value = if (cleared) StorageState.OK else StorageState.FAILED
        }
    }

    private fun persist(snapshot: PlayerProgress) {
        scope.launch {
            _storageState.value = if (store.writeProgress(snapshot)) StorageState.OK else StorageState.FAILED
        }
    }
}
