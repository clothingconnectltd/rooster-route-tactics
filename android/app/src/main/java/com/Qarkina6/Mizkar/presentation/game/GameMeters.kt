package com.Qarkina6.Mizkar.presentation.game

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Qarkina6.Mizkar.R
import com.Qarkina6.Mizkar.core.config.GameConfig
import com.Qarkina6.Mizkar.core.theme.BrassGold
import com.Qarkina6.Mizkar.core.theme.HairlineBorder
import com.Qarkina6.Mizkar.core.theme.LightGold
import com.Qarkina6.Mizkar.core.theme.RoosterRed
import com.Qarkina6.Mizkar.core.theme.SurfaceRaised
import com.Qarkina6.Mizkar.core.theme.TextMuted

@Composable
fun RouteMeterStrip(
    steps: Int,
    goal: Int,
    modifier: Modifier = Modifier
) {
    val fraction = if (goal <= 0) 0f else (steps.toFloat() / goal.toFloat()).coerceIn(0f, 1f)
    val animated by animateFloatAsState(
        targetValue = fraction,
        animationSpec = tween(durationMillis = 700),
        label = "routeMeter"
    )
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(30.dp)
    ) {
        val travel = maxWidth - 26.dp
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(SurfaceRaised)
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .width(travel * animated + 4.dp)
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(BrassGold)
        )
        Image(
            painter = painterResource(R.drawable.sprite_1),
            contentDescription = stringResource(R.string.cd_route_marker),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = travel * animated)
                .size(26.dp)
        )
        Text(
            text = stringResource(R.string.game_route_steps, steps, goal),
            fontSize = 10.sp,
            lineHeight = 13.sp,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextMuted,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

@Composable
fun FeatherChargeMeter(
    charge: Int,
    armed: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (armed) {
                stringResource(R.string.game_wild_ready)
            } else {
                stringResource(R.string.game_feather)
            },
            fontSize = 10.sp,
            lineHeight = 13.sp,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Bold,
            color = if (armed) LightGold else TextMuted,
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            for (index in 0 until GameConfig.FEATHER_MAX) {
                Box(
                    modifier = Modifier
                        .width(18.dp)
                        .height(8.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (index < charge) RoosterRed else SurfaceRaised)
                        .border(1.dp, HairlineBorder, RoundedCornerShape(3.dp))
                )
            }
        }
    }
}
