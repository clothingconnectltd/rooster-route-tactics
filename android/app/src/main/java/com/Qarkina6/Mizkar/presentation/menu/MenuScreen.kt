package com.Qarkina6.Mizkar.presentation.menu

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.Qarkina6.Mizkar.domain.model.LeaderboardRow
import com.Qarkina6.Mizkar.core.format.formatClock
import com.Qarkina6.Mizkar.core.format.formatCredits
import com.Qarkina6.Mizkar.core.theme.BackgroundLacquer
import com.Qarkina6.Mizkar.core.theme.BrassGold
import com.Qarkina6.Mizkar.core.theme.HairlineBorder
import com.Qarkina6.Mizkar.core.theme.LightGold
import com.Qarkina6.Mizkar.core.theme.RoosterRed
import com.Qarkina6.Mizkar.core.theme.SurfaceDeep
import com.Qarkina6.Mizkar.core.theme.SurfaceRaised
import com.Qarkina6.Mizkar.core.theme.SurfaceWarm
import com.Qarkina6.Mizkar.core.theme.TextMuted
import com.Qarkina6.Mizkar.core.theme.TextSecondary
import com.Qarkina6.Mizkar.core.ui.BrassPill
import com.Qarkina6.Mizkar.core.ui.PrimaryButton
import com.Qarkina6.Mizkar.core.ui.ScreenScaffold
import com.Qarkina6.Mizkar.core.ui.SectionHeader
import com.Qarkina6.Mizkar.core.ui.animateLoop

@Composable
fun MenuScreen(
    onPlay: () -> Unit,
    onLobby: () -> Unit,
    onCollection: () -> Unit,
    onMissions: () -> Unit,
    onRewards: () -> Unit,
    onResponsible: () -> Unit,
    onSettings: () -> Unit
) {
    val viewModel: MenuViewModel = viewModel(factory = AppViewModelFactory.Instance)
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    MenuContent(
        state = state,
        onPlay = {
            viewModel.endBreak()
            onPlay()
        },
        onClaimDaily = viewModel::claimDaily,
        onLobby = onLobby,
        onCollection = onCollection,
        onMissions = onMissions,
        onRewards = onRewards,
        onResponsible = onResponsible,
        onSettings = onSettings
    )
}

@Composable
private fun MenuContent(
    state: MenuUiState,
    onPlay: () -> Unit,
    onClaimDaily: () -> Unit,
    onLobby: () -> Unit,
    onCollection: () -> Unit,
    onMissions: () -> Unit,
    onRewards: () -> Unit,
    onResponsible: () -> Unit,
    onSettings: () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "menuPulse")
    val pulseRaw by transition.animateLoop(1200, 0.35f, 1f, "dailyPulse", RepeatMode.Reverse)
    val pulse = if (state.reducedMotion) 0.6f else pulseRaw

    ScreenScaffold(
        overlay = listOf(
            Color.Transparent,
            BackgroundLacquer.copy(alpha = 0.35f),
            BackgroundLacquer.copy(alpha = 0.72f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Image(
                painter = painterResource(R.drawable.icon_home),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 18.dp, top = 12.dp)
                    .size(28.dp)
            )
            BrassPill(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 16.dp, top = 10.dp)
                    .height(40.dp)
            ) {
                Row(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_star),
                        contentDescription = null,
                        tint = LightGold,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(7.dp))
                    Text(
                        text = stringResource(R.string.credits_value, formatCredits(state.balance)),
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = LightGold,
                        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                    )
                }
            }
            Image(
                painter = painterResource(if (state.avatarUnlocked) R.drawable.sprite_1 else R.drawable.logo),
                contentDescription = stringResource(R.string.cd_logo),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(132.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(18.dp, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(SurfaceDeep.copy(alpha = 0.97f))
                .border(
                    1.dp,
                    Brush.verticalGradient(listOf(BrassGold.copy(alpha = 0.32f), Color.Transparent)),
                    RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                )
                .padding(horizontal = 18.dp)
                .padding(top = 16.dp, bottom = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.menu_title),
                fontSize = 25.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp,
                color = LightGold
            )
            Text(
                text = stringResource(R.string.menu_tagline),
                fontSize = 11.sp,
                lineHeight = 15.sp,
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 5.dp)
            )

            PrimaryButton(
                text = if (state.breakRemainingSeconds > 0L) {
                    stringResource(R.string.cta_break, formatClock(state.breakRemainingSeconds))
                } else {
                    stringResource(R.string.cta_play)
                },
                onClick = onPlay,
                leadingIcon = R.drawable.ic_play,
                height = 58.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )

            DailyCard(
                state = state,
                pulse = pulse,
                onRewards = onRewards,
                onClaim = onClaimDaily,
                modifier = Modifier.padding(top = 12.dp)
            )

            LeaderboardStrip(
                state = state,
                modifier = Modifier.padding(top = 10.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                RouteTile(
                    label = stringResource(R.string.nav_lobby),
                    icon = R.drawable.ic_route,
                    onClick = onLobby,
                    modifier = Modifier.weight(1f)
                )
                RouteTile(
                    label = stringResource(R.string.nav_collection),
                    icon = R.drawable.ic_cards,
                    onClick = onCollection,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                RouteTile(
                    label = stringResource(R.string.nav_missions),
                    icon = R.drawable.ic_flag,
                    onClick = onMissions,
                    modifier = Modifier.weight(1f)
                )
                RouteTile(
                    label = stringResource(R.string.nav_responsible),
                    icon = R.drawable.ic_info,
                    onClick = onResponsible,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onSettings, modifier = Modifier.height(48.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.ic_settings),
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.nav_settings),
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted
                    )
                }
                Text(
                    text = stringResource(R.string.menu_footer),
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = RoosterRed
                )
            }
        }
    }
}

@Composable
private fun DailyCard(
    state: MenuUiState,
    pulse: Float,
    onRewards: () -> Unit,
    onClaim: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
            .clip(shape)
            .background(SurfaceWarm)
            .border(1.dp, LightGold.copy(alpha = if (state.dailyReady) pulse else 0.2f), shape)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_gift),
            contentDescription = null,
            tint = LightGold,
            modifier = Modifier.size(24.dp)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 10.dp)
        ) {
            Text(
                text = stringResource(
                    R.string.daily_card_title,
                    state.dailyDay,
                    formatCredits(state.dailyAmount)
                ),
                fontSize = 14.sp,
                lineHeight = 19.sp,
                fontWeight = FontWeight.Bold,
                color = LightGold
            )
            Text(
                text = if (state.dailyReady) {
                    stringResource(R.string.daily_ready)
                } else {
                    stringResource(R.string.daily_countdown, formatClock(state.dailyCountdownSeconds))
                },
                fontSize = 11.sp,
                lineHeight = 15.sp,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextMuted
            )
        }
        TextButton(
            onClick = if (state.dailyReady) onClaim else onRewards,
            modifier = Modifier.height(48.dp)
        ) {
            Text(
                text = if (state.dailyReady) {
                    stringResource(R.string.action_claim)
                } else {
                    stringResource(R.string.nav_rewards)
                },
                fontSize = 12.sp,
                lineHeight = 18.sp,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BrassGold
            )
        }
    }
}

@Composable
private fun LeaderboardStrip(
    state: MenuUiState,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SurfaceRaised.copy(alpha = 0.92f))
            .border(1.dp, HairlineBorder, shape)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        SectionHeader(title = stringResource(R.string.leaderboard_header))
        if (state.leaderboard.isEmpty()) {
            Text(
                text = stringResource(R.string.leaderboard_empty),
                fontSize = 11.sp,
                lineHeight = 16.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            )
        } else {
            val visible = pickVisibleRows(state.leaderboard)
            visible.forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(16.dp)
                            .background(if (row.isPlayer) BrassGold else Color.Transparent)
                    )
                    Text(
                        text = stringResource(R.string.leaderboard_rank, row.rank),
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        modifier = Modifier.padding(start = 7.dp, end = 9.dp)
                    )
                    Text(
                        text = row.displayName ?: stringResource(row.nameRes),
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        fontWeight = if (row.isPlayer) FontWeight.ExtraBold else FontWeight.Medium,
                        color = if (row.isPlayer) LightGold else TextSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = formatCredits(row.credits),
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (row.isPlayer) LightGold else TextMuted
                    )
                }
            }
        }
    }
}

private fun pickVisibleRows(rows: List<LeaderboardRow>): List<LeaderboardRow> {
    val playerIndex = rows.indexOfFirst { it.isPlayer }
    if (playerIndex < 0) return rows.take(3)
    val start = when {
        playerIndex <= 1 -> 0
        playerIndex >= rows.size - 1 -> rows.size - 3
        else -> playerIndex - 1
    }
    val safeStart = start.coerceAtLeast(0)
    return rows.subList(safeStart, minOf(safeStart + 3, rows.size))
}

@Composable
private fun RouteTile(
    label: String,
    icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = modifier
            .height(56.dp)
            .clip(shape)
            .background(SurfaceRaised)
            .border(1.dp, HairlineBorder, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = BrassGold,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(9.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary
        )
    }
}
