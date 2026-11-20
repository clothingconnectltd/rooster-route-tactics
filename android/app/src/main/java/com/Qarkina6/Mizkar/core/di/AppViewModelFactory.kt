package com.Qarkina6.Mizkar.core.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.Qarkina6.Mizkar.presentation.app.AppViewModel
import com.Qarkina6.Mizkar.presentation.bonus.BonusViewModel
import com.Qarkina6.Mizkar.presentation.collection.CollectionViewModel
import com.Qarkina6.Mizkar.presentation.game.GameViewModel
import com.Qarkina6.Mizkar.presentation.lobby.LobbyViewModel
import com.Qarkina6.Mizkar.presentation.menu.MenuViewModel
import com.Qarkina6.Mizkar.presentation.responsible.ResponsiblePlayViewModel
import com.Qarkina6.Mizkar.presentation.result.SessionResultViewModel
import com.Qarkina6.Mizkar.presentation.settings.SettingsViewModel
import com.Qarkina6.Mizkar.presentation.splash.SplashViewModel

object AppViewModelFactory {

    val Instance: ViewModelProvider.Factory = viewModelFactory {
        initializer {
            AppViewModel(ServiceLocator.session, ServiceLocator.clock)
        }
        initializer {
            SplashViewModel(ServiceLocator.settings.settings.value.reducedMotion)
        }
        initializer {
            MenuViewModel(
                ServiceLocator.progress,
                ServiceLocator.settings,
                ServiceLocator.leaderboard,
                ServiceLocator.catalog,
                ServiceLocator.claimDailyReward,
                ServiceLocator.clock
            )
        }
        initializer {
            LobbyViewModel(ServiceLocator.progress, ServiceLocator.catalog)
        }
        initializer {
            GameViewModel(
                ServiceLocator.progress,
                ServiceLocator.settings,
                ServiceLocator.catalog,
                ServiceLocator.session,
                ServiceLocator.spinReels,
                ServiceLocator.awardRouteSteps,
                ServiceLocator.rollCollectionDrop,
                ServiceLocator.updateMissions,
                ServiceLocator.completeSets,
                ServiceLocator.refillCredits,
                ServiceLocator.clock
            )
        }
        initializer {
            BonusViewModel(
                ServiceLocator.session,
                ServiceLocator.resolveBonus,
                ServiceLocator.clock
            )
        }
        initializer {
            SessionResultViewModel(
                ServiceLocator.session,
                ServiceLocator.progress,
                ServiceLocator.settings,
                ServiceLocator.catalog
            )
        }
        initializer {
            CollectionViewModel(
                ServiceLocator.progress,
                ServiceLocator.catalog,
                ServiceLocator.session,
                ServiceLocator.claimMission,
                ServiceLocator.claimDailyReward,
                ServiceLocator.clock
            )
        }
        initializer {
            ResponsiblePlayViewModel(
                ServiceLocator.progress,
                ServiceLocator.settings,
                ServiceLocator.session,
                ServiceLocator.clock
            )
        }
        initializer {
            SettingsViewModel(ServiceLocator.settings, ServiceLocator.progress)
        }
    }
}
