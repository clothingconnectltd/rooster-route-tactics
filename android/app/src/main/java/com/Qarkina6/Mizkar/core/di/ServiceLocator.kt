package com.Qarkina6.Mizkar.core.di

import android.content.Context
import com.Qarkina6.Mizkar.R
import com.Qarkina6.Mizkar.data.local.PreferencesStore
import com.Qarkina6.Mizkar.data.repository.CatalogRepositoryImpl
import com.Qarkina6.Mizkar.data.repository.LeaderboardRepositoryImpl
import com.Qarkina6.Mizkar.data.repository.ProgressRepositoryImpl
import com.Qarkina6.Mizkar.data.repository.RunSessionRepositoryImpl
import com.Qarkina6.Mizkar.data.repository.SettingsRepositoryImpl
import com.Qarkina6.Mizkar.data.repository.SystemClock
import com.Qarkina6.Mizkar.domain.repository.CatalogRepository
import com.Qarkina6.Mizkar.domain.repository.Clock
import com.Qarkina6.Mizkar.domain.repository.LeaderboardRepository
import com.Qarkina6.Mizkar.domain.repository.ProgressRepository
import com.Qarkina6.Mizkar.domain.repository.RunSessionRepository
import com.Qarkina6.Mizkar.domain.repository.SettingsRepository
import com.Qarkina6.Mizkar.domain.usecase.AwardRouteStepsUseCase
import com.Qarkina6.Mizkar.domain.usecase.ClaimDailyRewardUseCase
import com.Qarkina6.Mizkar.domain.usecase.ClaimMissionUseCase
import com.Qarkina6.Mizkar.domain.usecase.CompleteSetsUseCase
import com.Qarkina6.Mizkar.domain.usecase.EvaluateLinesUseCase
import com.Qarkina6.Mizkar.domain.usecase.RefillCreditsUseCase
import com.Qarkina6.Mizkar.domain.usecase.ResolveBonusUseCase
import com.Qarkina6.Mizkar.domain.usecase.RollCollectionDropUseCase
import com.Qarkina6.Mizkar.domain.usecase.SpinReelsUseCase
import com.Qarkina6.Mizkar.domain.usecase.UpdateMissionsUseCase

object ServiceLocator {

    private var catalogRepository: CatalogRepository? = null
    private var progressRepository: ProgressRepository? = null
    private var settingsRepository: SettingsRepository? = null
    private var leaderboardRepository: LeaderboardRepository? = null
    private var runSessionRepository: RunSessionRepository? = null
    private var appClock: Clock? = null

    val catalog: CatalogRepository get() = requireNotNull(catalogRepository)
    val progress: ProgressRepository get() = requireNotNull(progressRepository)
    val settings: SettingsRepository get() = requireNotNull(settingsRepository)
    val leaderboard: LeaderboardRepository get() = requireNotNull(leaderboardRepository)
    val session: RunSessionRepository get() = requireNotNull(runSessionRepository)
    val clock: Clock get() = requireNotNull(appClock)

    val evaluateLines = EvaluateLinesUseCase()
    val spinReels = SpinReelsUseCase(evaluateLines)
    val resolveBonus = ResolveBonusUseCase()
    val awardRouteSteps = AwardRouteStepsUseCase()
    val rollCollectionDrop = RollCollectionDropUseCase()
    val updateMissions = UpdateMissionsUseCase()
    val claimMission = ClaimMissionUseCase()
    val claimDailyReward = ClaimDailyRewardUseCase()
    val completeSets = CompleteSetsUseCase()
    val refillCredits = RefillCreditsUseCase()

    fun init(context: Context) {
        if (progressRepository != null) return
        val appContext = context.applicationContext
        val store = PreferencesStore(appContext)
        val catalogImpl = CatalogRepositoryImpl()
        val clockImpl = SystemClock()
        catalogRepository = catalogImpl
        appClock = clockImpl
        progressRepository = ProgressRepositoryImpl(store, catalogImpl.routes.size)
        settingsRepository = SettingsRepositoryImpl(store, appContext.getString(R.string.player_default_name))
        leaderboardRepository = LeaderboardRepositoryImpl(catalogImpl)
        runSessionRepository = RunSessionRepositoryImpl(clockImpl)
    }
}
