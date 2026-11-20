package com.Qarkina6.Mizkar.presentation.lobby

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
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
import com.Qarkina6.Mizkar.core.theme.HairlineBorder
import com.Qarkina6.Mizkar.core.theme.LightGold
import com.Qarkina6.Mizkar.core.theme.SurfaceDeep
import com.Qarkina6.Mizkar.core.theme.SurfaceWarm
import com.Qarkina6.Mizkar.core.theme.TextMuted
import com.Qarkina6.Mizkar.core.theme.TextSecondary
import com.Qarkina6.Mizkar.core.ui.PrimaryButton
import com.Qarkina6.Mizkar.core.ui.ProgressMeter
import com.Qarkina6.Mizkar.core.ui.ScreenScaffold
import com.Qarkina6.Mizkar.core.ui.SecondaryButton
import com.Qarkina6.Mizkar.core.ui.SectionHeader
import com.Qarkina6.Mizkar.core.ui.TopBarRow

@Composable
fun LobbyScreen(
    onMenu: () -> Unit,
    onPlayRoute: (Int) -> Unit,
    onPreview: () -> Unit
) {
    val viewModel: LobbyViewModel = viewModel(factory = AppViewModelFactory.Instance)
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LobbyContent(
        state = state,
        onMenu = onMenu,
        onPlayRoute = onPlayRoute,
        onPreview = onPreview
    )
}

@Composable
private fun LobbyContent(
    state: LobbyUiState,
    onMenu: () -> Unit,
    onPlayRoute: (Int) -> Unit,
    onPreview: () -> Unit
) {
    ScreenScaffold(
        overlay = listOf(
            BackgroundLacquer.copy(alpha = 0.90f),
            SurfaceWarm.copy(alpha = 0.80f),
            BackgroundLacquer.copy(alpha = 0.95f)
        )
    ) {
        TopBarRow(
            title = stringResource(R.string.lobby_title),
            menuLabel = stringResource(R.string.nav_menu),
            onMenu = onMenu,
            balance = stringResource(R.string.credits_value, formatCredits(state.balance))
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(state.routes) { route ->
                RouteCard(route = route, onPlayRoute = onPlayRoute)
            }
        }
        FeaturePreviewPanel(
            onPreview = onPreview,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 12.dp)
        )
    }
}

@Composable
private fun RouteCard(
    route: LobbyRouteUi,
    onPlayRoute: (Int) -> Unit
) {
    val shape = RoundedCornerShape(14.dp)
    val stateLabel = if (route.unlocked) {
        stringResource(R.string.lobby_state_open)
    } else {
        stringResource(R.string.lobby_state_locked)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .alpha(if (route.unlocked) 1f else 0.45f)
            .clip(shape)
            .background(SurfaceDeep.copy(alpha = 0.94f))
            .border(1.dp, if (route.unlocked) BrassGold.copy(alpha = 0.32f) else HairlineBorder, shape)
            .padding(horizontal = 12.dp)
            .semantics { stateDescription = stateLabel },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(SurfaceWarm)
                .border(2.dp, BrassGold.copy(alpha = 0.6f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(route.numeralRes),
                fontSize = 18.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = LightGold
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 11.dp)
        ) {
            Text(
                text = stringResource(route.nameRes),
                fontSize = 15.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Bold,
                color = LightGold
            )
            Text(
                text = if (route.steps == 0) {
                    stringResource(R.string.lobby_not_started)
                } else {
                    stringResource(R.string.lobby_steps, route.steps, route.goal)
                },
                fontSize = 11.sp,
                lineHeight = 15.sp,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 2.dp)
            )
            ProgressMeter(
                progress = route.steps.toFloat() / route.goal.toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 7.dp)
            )
        }
        if (route.unlocked) {
            PrimaryButton(
                text = stringResource(R.string.cta_play),
                onClick = { onPlayRoute(route.index) },
                height = 48.dp,
                textSize = 13,
                modifier = Modifier.width(118.dp)
            )
        } else {
            Column(
                modifier = Modifier.width(118.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_lock),
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = stringResource(R.string.lobby_locked),
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun FeaturePreviewPanel(
    onPreview: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SurfaceWarm)
            .border(1.dp, BrassGold.copy(alpha = 0.35f), shape)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        SectionHeader(title = stringResource(R.string.lobby_preview_header))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            PreviewChip(R.drawable.sprite_2, stringResource(R.string.lobby_chip_bonus), stringResource(R.string.cd_symbol_sign))
            PreviewChip(R.drawable.sprite_1, stringResource(R.string.lobby_chip_wild), stringResource(R.string.cd_symbol_rooster))
            PreviewChip(R.drawable.sprite_3, stringResource(R.string.lobby_chip_hen), stringResource(R.string.cd_symbol_hen))
        }
        SecondaryButton(
            text = stringResource(R.string.lobby_preview_cta),
            onClick = onPreview,
            leadingIcon = R.drawable.ic_gift,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        )
    }
}

@Composable
private fun PreviewChip(art: Int, label: String, description: String) {
    Column(
        modifier = Modifier.width(96.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(art),
            contentDescription = description,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(38.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 9.sp,
            lineHeight = 12.sp,
            letterSpacing = 0.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}
