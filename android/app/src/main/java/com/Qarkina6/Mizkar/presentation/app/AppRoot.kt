package com.Qarkina6.Mizkar.presentation.app

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Qarkina6.Mizkar.core.di.AppViewModelFactory
import com.Qarkina6.Mizkar.core.navigation.Screen
import com.Qarkina6.Mizkar.core.theme.BackgroundLacquer
import com.Qarkina6.Mizkar.presentation.bonus.BonusScreen
import com.Qarkina6.Mizkar.presentation.collection.CollectionScreen
import com.Qarkina6.Mizkar.presentation.game.GameScreen
import com.Qarkina6.Mizkar.presentation.lobby.LobbyScreen
import com.Qarkina6.Mizkar.presentation.menu.MenuScreen
import com.Qarkina6.Mizkar.presentation.responsible.ResponsiblePlayScreen
import com.Qarkina6.Mizkar.presentation.result.SessionResultScreen
import com.Qarkina6.Mizkar.presentation.settings.SettingsScreen
import com.Qarkina6.Mizkar.presentation.splash.SplashScreen

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory.Instance)
    val screen by appViewModel.screen.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = remember(context) { context.findHostActivity() }

    BackHandler(enabled = true) {
        if (!appViewModel.back()) activity?.finish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLacquer)
    ) {
        AnimatedContent(
            targetState = screen,
            transitionSpec = {
                fadeIn(animationSpec = tween(380)) togetherWith fadeOut(animationSpec = tween(280))
            },
            label = "screenTransition",
            modifier = Modifier.fillMaxSize()
        ) { target ->
            when (target) {
                Screen.Splash -> SplashScreen(
                    onFinished = { appViewModel.replace(Screen.Menu) }
                )
                Screen.Menu -> MenuScreen(
                    onPlay = { appViewModel.navigate(Screen.Game) },
                    onLobby = { appViewModel.navigate(Screen.Lobby) },
                    onCollection = { appViewModel.openCollection(0) },
                    onMissions = { appViewModel.openCollection(1) },
                    onRewards = { appViewModel.openCollection(2) },
                    onResponsible = { appViewModel.navigate(Screen.ResponsiblePlay) },
                    onSettings = { appViewModel.navigate(Screen.Settings) }
                )
                Screen.Lobby -> LobbyScreen(
                    onMenu = { appViewModel.backTo(Screen.Menu) },
                    onPlayRoute = { index -> appViewModel.openRoute(index) },
                    onPreview = { appViewModel.previewBonus() }
                )
                Screen.Game -> GameScreen(
                    onMenu = { appViewModel.backTo(Screen.Menu) },
                    onBonus = { appViewModel.navigate(Screen.Bonus) },
                    onRunFinished = { appViewModel.navigate(Screen.SessionResult) },
                    onResponsible = { appViewModel.navigate(Screen.ResponsiblePlay) }
                )
                Screen.Bonus -> BonusScreen(
                    onDone = { appViewModel.back() }
                )
                Screen.SessionResult -> SessionResultScreen(
                    onSpinAgain = { appViewModel.backTo(Screen.Game) },
                    onCollection = { appViewModel.openCollection(0) },
                    onMenu = { appViewModel.backTo(Screen.Menu) }
                )
                Screen.Collection -> CollectionScreen(
                    onMenu = { appViewModel.backTo(Screen.Menu) }
                )
                Screen.ResponsiblePlay -> ResponsiblePlayScreen(
                    onMenu = { appViewModel.backTo(Screen.Menu) },
                    onBreakTaken = { appViewModel.backTo(Screen.Menu) }
                )
                Screen.Settings -> SettingsScreen(
                    onMenu = { appViewModel.backTo(Screen.Menu) },
                    onResponsible = { appViewModel.navigate(Screen.ResponsiblePlay) }
                )
            }
        }
    }
}

private fun Context.findHostActivity(): Activity? {
    var current: Context = this
    while (current is ContextWrapper) {
        if (current is Activity) return current
        current = current.baseContext
    }
    return null
}
