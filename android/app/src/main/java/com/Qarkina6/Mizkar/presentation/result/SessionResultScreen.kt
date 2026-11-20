package com.Qarkina6.Mizkar.presentation.result

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Qarkina6.Mizkar.R
import com.Qarkina6.Mizkar.core.di.AppViewModelFactory
import com.Qarkina6.Mizkar.core.format.formatCredits
import com.Qarkina6.Mizkar.core.theme.BackgroundLacquer
import com.Qarkina6.Mizkar.core.theme.BrassGold
import com.Qarkina6.Mizkar.core.theme.LightGold
import com.Qarkina6.Mizkar.core.theme.RoosterRed
import com.Qarkina6.Mizkar.core.theme.SurfaceDeep
import com.Qarkina6.Mizkar.core.theme.SurfaceWarm
import com.Qarkina6.Mizkar.core.theme.TextMuted
import com.Qarkina6.Mizkar.core.theme.TextSecondary
import com.Qarkina6.Mizkar.core.ui.PrimaryButton
import com.Qarkina6.Mizkar.core.ui.ProgressMeter
import com.Qarkina6.Mizkar.core.ui.ScreenScaffold
import com.Qarkina6.Mizkar.core.ui.SecondaryButton
import com.Qarkina6.Mizkar.core.ui.StatCard

@Composable
fun SessionResultScreen(
    onSpinAgain: () -> Unit,
    onCollection: () -> Unit,
    onMenu: () -> Unit
) {
    val viewModel: SessionResultViewModel = viewModel(factory = AppViewModelFactory.Instance)
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    SessionResultContent(
        state = state,
        onSpinAgain = onSpinAgain,
        onCollection = onCollection,
        onMenu = onMenu
    )
}

@Composable
private fun SessionResultContent(
    state: SessionResultUiState,
    onSpinAgain: () -> Unit,
    onCollection: () -> Unit,
    onMenu: () -> Unit
) {
    val headlineColor = when (state.tone) {
        ResultTone.WIN -> LightGold
        ResultTone.EVEN -> TextSecondary
        ResultTone.LOSS -> RoosterRed
    }
    val showWon = state.won > 0L
    val showSteps = state.steps > 0
    val showBest = state.bestSpin > 0L
    val visibleStats = (if (showWon) 1 else 0) + (if (showSteps) 1 else 0) + (if (showBest) 1 else 0)

    ScreenScaffold(
        overlay = listOf(
            BackgroundLacquer.copy(alpha = 0.95f),
            SurfaceWarm.copy(alpha = 0.82f),
            BackgroundLacquer.copy(alpha = 0.95f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = stringResource(R.string.cd_logo),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(84.dp)
            )
            Text(
                text = stringResource(state.headlineRes),
                fontSize = 28.sp,
                lineHeight = 34.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.ExtraBold,
                color = headlineColor,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .semantics { liveRegion = LiveRegionMode.Polite }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .shadow(14.dp, RoundedCornerShape(18.dp))
                    .clip(RoundedCornerShape(18.dp))
                    .background(SurfaceDeep.copy(alpha = 0.95f))
                    .border(1.dp, BrassGold.copy(alpha = 0.32f), RoundedCornerShape(18.dp))
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (visibleStats >= 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (showWon) {
                            StatCard(
                                label = stringResource(R.string.stat_won),
                                value = formatCredits(state.won),
                                accent = LightGold,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (showSteps) {
                            StatCard(
                                label = stringResource(R.string.stat_steps),
                                value = state.steps.toString(),
                                accent = BrassGold,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (showBest) {
                            StatCard(
                                label = stringResource(R.string.stat_best_spin),
                                value = formatCredits(state.bestSpin),
                                accent = TextSecondary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                } else {
                    Text(
                        text = stringResource(R.string.result_no_pays),
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                }

                if (state.routeNameRes != 0) {
                    Text(
                        text = stringResource(state.routeNameRes),
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                    ProgressMeter(
                        progress = state.routeSteps.toFloat() / state.routeGoal.toFloat(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                    )
                    Text(
                        text = stringResource(R.string.game_route_steps, state.routeSteps, state.routeGoal),
                        fontSize = 10.sp,
                        lineHeight = 14.sp,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                if (state.unlockedRouteNameRes != 0) {
                    Text(
                        text = stringResource(
                            R.string.result_route_unlocked,
                            stringResource(state.unlockedRouteNameRes)
                        ),
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = LightGold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceWarm)
                            .border(1.dp, LightGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                if (state.cardNameRes != 0 && state.cardArtRes != 0) {
                    Row(
                        modifier = Modifier.padding(top = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(state.cardArtRes),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(34.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(
                                R.string.result_card_drop,
                                stringResource(state.cardNameRes)
                            ),
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            PrimaryButton(
                text = stringResource(R.string.result_spin_again),
                onClick = onSpinAgain,
                leadingIcon = R.drawable.ic_refresh,
                height = 58.dp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SecondaryButton(
                    text = stringResource(R.string.nav_collection),
                    onClick = onCollection,
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    text = stringResource(R.string.nav_menu),
                    onClick = onMenu,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
