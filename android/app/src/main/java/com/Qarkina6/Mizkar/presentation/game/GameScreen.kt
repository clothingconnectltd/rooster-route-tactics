package com.Qarkina6.Mizkar.presentation.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.Qarkina6.Mizkar.core.config.GameConfig
import com.Qarkina6.Mizkar.core.di.AppViewModelFactory
import com.Qarkina6.Mizkar.core.format.formatCredits
import com.Qarkina6.Mizkar.core.theme.BackgroundLacquer
import com.Qarkina6.Mizkar.core.theme.BrassGold
import com.Qarkina6.Mizkar.core.theme.HairlineBorder
import com.Qarkina6.Mizkar.core.theme.LightGold
import com.Qarkina6.Mizkar.core.theme.RoosterRed
import com.Qarkina6.Mizkar.core.theme.SurfaceDeep
import com.Qarkina6.Mizkar.core.theme.SurfaceWarm
import com.Qarkina6.Mizkar.core.theme.TextMuted
import com.Qarkina6.Mizkar.core.theme.TextSecondary
import com.Qarkina6.Mizkar.core.ui.PrimaryButton
import com.Qarkina6.Mizkar.core.ui.ScreenScaffold
import com.Qarkina6.Mizkar.core.ui.SecondaryButton
import com.Qarkina6.Mizkar.core.ui.StatCard
import com.Qarkina6.Mizkar.presentation.dialog.HowToPlayDialog
import com.Qarkina6.Mizkar.presentation.dialog.PauseDialog

@Composable
fun GameScreen(
    onMenu: () -> Unit,
    onBonus: () -> Unit,
    onRunFinished: () -> Unit,
    onResponsible: () -> Unit
) {
    val viewModel: GameViewModel = viewModel(factory = AppViewModelFactory.Instance)
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is GameEvent.BonusTriggered -> onBonus()
                is GameEvent.RunFinished -> onRunFinished()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.pause() }
    }

    GameContent(
        state = state,
        onMenu = onMenu,
        onSpin = viewModel::spin,
        onRefill = viewModel::refill,
        onPause = viewModel::openPause,
        onHowTo = viewModel::openHowTo
    )

    if (state.showPause) {
        PauseDialog(
            onResume = viewModel::closePause,
            onResponsible = {
                viewModel.closePause()
                onResponsible()
            },
            onMenu = {
                viewModel.closePause()
                onMenu()
            },
            onEndRun = viewModel::endRunEarly,
            onDismiss = viewModel::closePause
        )
    }
    if (state.showHowTo) {
        HowToPlayDialog(onClose = viewModel::closeHowTo)
    }
}

@Composable
private fun GameContent(
    state: GameUiState,
    onMenu: () -> Unit,
    onSpin: () -> Unit,
    onRefill: () -> Unit,
    onPause: () -> Unit,
    onHowTo: () -> Unit
) {
    ScreenScaffold(
        overlay = listOf(
            BackgroundLacquer.copy(alpha = 0.88f),
            SurfaceWarm.copy(alpha = 0.72f),
            BackgroundLacquer.copy(alpha = 0.95f)
        )
    ) {
        GameHeader(state = state, onMenu = onMenu, onPause = onPause)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val available = maxWidth.value - 32f
                val bounded = if (available < CABINET_MAX_DP) available else CABINET_MAX_DP
                val cell = kotlin.math.floor((bounded - CABINET_FRAME_DP * 2f) / GameConfig.REELS)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    WinBanner(state = state)
                    ReelCabinet(
                        state = state,
                        cellSize = cell,
                        reducedMotion = state.reducedMotion,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    if (state.showRouteMeter) {
                        RouteMeterStrip(
                            steps = state.routeSteps,
                            goal = state.routeGoal,
                            modifier = Modifier
                                .width((cell * GameConfig.REELS + CABINET_FRAME_DP * 2f).dp)
                                .padding(top = 6.dp)
                        )
                    }
                    FeatherChargeMeter(
                        charge = state.featherCharge,
                        armed = state.wildDropArmed,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        StatStrip(state = state)

        ControlsBlock(
            state = state,
            onSpin = onSpin,
            onRefill = onRefill,
            onMenu = onMenu,
            onHowTo = onHowTo
        )
    }
}

@Composable
private fun GameHeader(
    state: GameUiState,
    onMenu: () -> Unit,
    onPause: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp)
            .background(BackgroundLacquer.copy(alpha = 0.72f))
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onMenu, modifier = Modifier.height(48.dp)) {
            Image(
                painter = painterResource(R.drawable.icon_home),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = stringResource(R.string.nav_menu),
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = TextSecondary
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.routeNameRes != 0) {
                Text(
                    text = stringResource(state.routeNameRes),
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            }
            Text(
                text = stringResource(R.string.game_round, state.roundIndex, GameConfig.SPINS_PER_RUN),
                fontSize = 10.sp,
                lineHeight = 14.sp,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextMuted
            )
        }
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(SurfaceWarm)
                .border(1.dp, BrassGold.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                .clickable(onClick = onPause),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.icon_pause),
                contentDescription = stringResource(R.string.cd_pause),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = stringResource(R.string.credits_value, formatCredits(state.balance)),
            fontSize = 14.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = LightGold,
            modifier = Modifier
                .padding(start = 8.dp)
                .semantics { liveRegion = LiveRegionMode.Polite }
        )
    }
}

@Composable
private fun WinBanner(state: GameUiState) {
    val shape = RoundedCornerShape(18.dp)
    Box(
        modifier = Modifier.height(34.dp),
        contentAlignment = Alignment.Center
    ) {
        if (state.showWinBanner && state.winSymbolRes != 0) {
            Text(
                text = stringResource(
                    R.string.game_win_banner,
                    stringResource(state.winSymbolRes),
                    formatCredits(state.lastWin)
                ),
                fontSize = 13.sp,
                lineHeight = 18.sp,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold,
                color = LightGold,
                modifier = Modifier
                    .clip(shape)
                    .background(SurfaceWarm)
                    .border(1.dp, LightGold.copy(alpha = 0.6f), shape)
                    .padding(horizontal = 14.dp, vertical = 7.dp)
                    .semantics { liveRegion = LiveRegionMode.Polite }
            )
        } else if (state.showNoPay) {
            Text(
                text = stringResource(R.string.game_no_pay),
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextMuted
            )
        } else if (state.nearWinReel >= 0) {
            Text(
                text = stringResource(R.string.game_near_win),
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.SemiBold,
                color = RoosterRed
            )
        }
    }
}

@Composable
private fun StatStrip(state: GameUiState) {
    val showWin = state.lastWin > 0L
    val showSteps = state.routeSteps > 0
    val visible = 1 + (if (showWin) 1 else 0) + (if (showSteps) 1 else 0)
    if (visible < 2) return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(
            label = stringResource(R.string.stat_bet),
            value = formatCredits(GameConfig.BET),
            accent = TextSecondary,
            modifier = Modifier.weight(1f)
        )
        if (showWin) {
            StatCard(
                label = stringResource(R.string.stat_last_win),
                value = formatCredits(state.lastWin),
                accent = LightGold,
                modifier = Modifier.weight(1f)
            )
        }
        if (showSteps) {
            StatCard(
                label = stringResource(R.string.stat_steps),
                value = state.routeSteps.toString(),
                accent = BrassGold,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ControlsBlock(
    state: GameUiState,
    onSpin: () -> Unit,
    onRefill: () -> Unit,
    onMenu: () -> Unit,
    onHowTo: () -> Unit
) {
    val shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SurfaceDeep.copy(alpha = 0.97f))
            .border(1.dp, HairlineBorder, shape)
            .padding(horizontal = 18.dp)
            .padding(top = 10.dp, bottom = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (state.lowCredits) {
                stringResource(R.string.game_low_credits)
            } else {
                stringResource(R.string.game_bet_line, formatCredits(GameConfig.BET))
            },
            fontSize = 11.sp,
            lineHeight = 15.sp,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (state.lowCredits) RoosterRed else TextMuted,
            textAlign = TextAlign.Center
        )
        if (state.showHint && !state.isSpinning) {
            Text(
                text = stringResource(R.string.game_hint),
                fontSize = 11.sp,
                lineHeight = 15.sp,
                letterSpacing = 0.5.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        if (state.lowCredits) {
            SecondaryButton(
                text = stringResource(R.string.game_refill),
                onClick = onRefill,
                leadingIcon = R.drawable.ic_gift,
                height = 56.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
            SecondaryButton(
                text = stringResource(R.string.nav_menu),
                onClick = onMenu,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        } else {
            PrimaryButton(
                text = stringResource(R.string.game_spin),
                onClick = onSpin,
                enabled = !state.isSpinning,
                leadingIcon = R.drawable.ic_refresh,
                height = 62.dp,
                textSize = 20,
                stateLabel = if (state.isSpinning) {
                    stringResource(R.string.game_state_turning)
                } else {
                    stringResource(R.string.game_state_ready)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
            TextButton(onClick = onHowTo, modifier = Modifier.height(48.dp)) {
                Text(
                    text = stringResource(R.string.game_how_to),
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )
            }
        }
    }
}
