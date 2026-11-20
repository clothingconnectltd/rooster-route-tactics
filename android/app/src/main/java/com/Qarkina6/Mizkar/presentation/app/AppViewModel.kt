package com.Qarkina6.Mizkar.presentation.app

import androidx.lifecycle.ViewModel
import com.Qarkina6.Mizkar.core.config.GameConfig
import com.Qarkina6.Mizkar.core.navigation.Screen
import com.Qarkina6.Mizkar.domain.model.BonusRequest
import com.Qarkina6.Mizkar.domain.repository.Clock
import com.Qarkina6.Mizkar.domain.repository.RunSessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel(
    private val session: RunSessionRepository,
    private val clock: Clock
) : ViewModel() {

    private val backStack = ArrayDeque<Screen>()

    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    fun navigate(target: Screen) {
        val current = _screen.value
        if (current == target) return
        if (current != Screen.Splash) backStack.addLast(current)
        _screen.value = target
    }

    fun replace(target: Screen) {
        backStack.clear()
        _screen.value = target
    }

    fun back(): Boolean {
        val previous = backStack.removeLastOrNull() ?: return false
        _screen.value = previous
        return true
    }

    fun backTo(target: Screen) {
        if (_screen.value == target) return
        while (backStack.isNotEmpty()) {
            val top = backStack.removeLast()
            if (top == target) {
                _screen.value = target
                return
            }
        }
        _screen.value = target
    }

    fun openCollection(tab: Int) {
        session.selectCollectionTab(tab)
        navigate(Screen.Collection)
    }

    fun openRoute(index: Int) {
        session.selectRoute(index)
        navigate(Screen.Game)
    }

    fun previewBonus() {
        session.requestBonus(BonusRequest(GameConfig.BET, true, clock.now()))
        navigate(Screen.Bonus)
    }
}
