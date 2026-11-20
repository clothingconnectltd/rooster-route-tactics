package com.Qarkina6.Mizkar.core.navigation

sealed interface Screen {
    data object Splash : Screen
    data object Menu : Screen
    data object Lobby : Screen
    data object Game : Screen
    data object Bonus : Screen
    data object SessionResult : Screen
    data object Collection : Screen
    data object ResponsiblePlay : Screen
    data object Settings : Screen
}
