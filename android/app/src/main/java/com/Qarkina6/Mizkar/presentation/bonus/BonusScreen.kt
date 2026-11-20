package com.Qarkina6.Mizkar.presentation.bonus

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
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
import com.Qarkina6.Mizkar.core.theme.LightGold
import com.Qarkina6.Mizkar.core.theme.RoosterRed
import com.Qarkina6.Mizkar.core.theme.SurfaceRaised
import com.Qarkina6.Mizkar.core.theme.SurfaceWarm
import com.Qarkina6.Mizkar.core.theme.TextMuted
import com.Qarkina6.Mizkar.core.theme.TextSecondary
import com.Qarkina6.Mizkar.core.ui.PrimaryButton
import com.Qarkina6.Mizkar.core.ui.ScreenScaffold
import com.Qarkina6.Mizkar.core.ui.TopBarRow
import com.Qarkina6.Mizkar.domain.model.BonusTileKind

@Composable
fun BonusScreen(onDone: () -> Unit) {
    val viewModel: BonusViewModel = viewModel(factory = AppViewModelFactory.Instance)
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    BonusContent(
        state = state,
        onPick = viewModel::pick,
        onCollect = {
            viewModel.collect()
            onDone()
        }
    )
}

@Composable
private fun BonusContent(
    state: BonusUiState,
    onPick: (Int) -> Unit,
    onCollect: () -> Unit
) {
    ScreenScaffold(
        overlay = listOf(
            SurfaceWarm.copy(alpha = 0.92f),
            BackgroundLacquer.copy(alpha = 0.86f),
            BackgroundLacquer.copy(alpha = 0.96f)
        )
    ) {
        TopBarRow(
            title = stringResource(R.string.bonus_title),
            menuLabel = stringResource(R.string.nav_back),
            onMenu = onCollect
        )
        Text(
            text = if (state.preview) {
                stringResource(R.string.bonus_preview_note)
            } else {
                stringResource(R.string.bonus_subtitle)
            },
            fontSize = 12.sp,
            lineHeight = 17.sp,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (state.preview) RoosterRed else TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (rowIndex in 0 until 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        for (columnIndex in 0 until 3) {
                            val index = rowIndex * 3 + columnIndex
                            BonusTileView(
                                tile = state.tiles.getOrNull(index),
                                onPick = { onPick(index) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (state.restored) {
                    stringResource(R.string.bonus_restored)
                } else {
                    stringResource(R.string.bonus_total, formatCredits(state.total))
                },
                fontSize = 17.sp,
                lineHeight = 22.sp,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.ExtraBold,
                color = LightGold,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
            )
            if (!state.preview && !state.finished) {
                Text(
                    text = stringResource(R.string.bonus_picks_left, state.picksLeft),
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            PrimaryButton(
                text = if (state.preview) {
                    stringResource(R.string.nav_back)
                } else {
                    stringResource(R.string.bonus_collect)
                },
                onClick = onCollect,
                height = 56.dp,
                leadingIcon = R.drawable.ic_check,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            )
        }
    }
}

@Composable
private fun BonusTileView(
    tile: BonusTileUi?,
    onPick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    val revealed = tile?.revealed == true
    val flip by animateFloatAsState(
        targetValue = if (revealed) 180f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "tileFlip"
    )
    val stateLabel = if (revealed) {
        stringResource(R.string.bonus_state_open)
    } else {
        stringResource(R.string.bonus_state_closed)
    }
    Box(
        modifier = modifier
            .height(96.dp)
            .graphicsLayer { rotationY = if (flip > 90f) flip - 180f else flip }
            .clip(shape)
            .background(if (revealed) SurfaceWarm else SurfaceRaised)
            .border(2.dp, BrassGold.copy(alpha = if (revealed) 0.7f else 0.4f), shape)
            .clickable(onClick = onPick)
            .semantics { stateDescription = stateLabel },
        contentAlignment = Alignment.Center
    ) {
        if (tile == null) {
            Icon(
                painter = painterResource(R.drawable.ic_lock),
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(26.dp)
            )
        } else if (!revealed) {
            Image(
                painter = painterResource(R.drawable.sprite_2),
                contentDescription = stringResource(R.string.cd_symbol_sign),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(42.dp)
            )
        } else {
            when (tile.kind) {
                BonusTileKind.CREDITS -> Text(
                    text = stringResource(R.string.credits_gain, formatCredits(tile.credits)),
                    fontSize = 17.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = LightGold
                )
                BonusTileKind.STEP -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(R.drawable.sprite_1),
                        contentDescription = stringResource(R.string.cd_symbol_rooster),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(34.dp)
                    )
                    Text(
                        text = stringResource(R.string.bonus_reveal_step),
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrassGold
                    )
                }
                BonusTileKind.STOP -> Text(
                    text = stringResource(R.string.bonus_reveal_stop),
                    fontSize = 17.sp,
                    lineHeight = 22.sp,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = RoosterRed
                )
            }
        }
    }
}
