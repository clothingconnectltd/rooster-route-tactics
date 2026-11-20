package com.Qarkina6.Mizkar.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.Qarkina6.Mizkar.core.theme.BrassGold
import com.Qarkina6.Mizkar.core.theme.DeepBrass
import com.Qarkina6.Mizkar.core.theme.HairlineBorder
import com.Qarkina6.Mizkar.core.theme.HighlightEdge
import com.Qarkina6.Mizkar.core.theme.LightGold
import com.Qarkina6.Mizkar.core.theme.SurfaceRaised
import com.Qarkina6.Mizkar.core.theme.SurfaceWarm

@Composable
fun CabinetPanel(
    modifier: Modifier = Modifier,
    radius: Dp = 14.dp,
    bezel: Brush = Brush.linearGradient(listOf(LightGold, BrassGold, DeepBrass)),
    fill: Color = SurfaceWarm,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(radius)
    Box(
        modifier = modifier
            .shadow(16.dp, shape, ambientColor = Color.Black, spotColor = Color.Black)
            .clip(shape)
            .background(fill)
            .border(2.dp, bezel, shape),
        content = content
    )
}

@Composable
fun GlassPanel(
    modifier: Modifier = Modifier,
    radius: Dp = 14.dp,
    fill: Color = SurfaceRaised.copy(alpha = 0.92f),
    borderColor: Color = HairlineBorder,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(radius)
    Box(
        modifier = modifier
            .clip(shape)
            .background(fill)
            .border(1.dp, borderColor, shape),
        content = content
    )
}

@Composable
fun BrassPill(
    modifier: Modifier = Modifier,
    radius: Dp = 12.dp,
    borderColor: Color = BrassGold.copy(alpha = 0.45f),
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(radius)
    Box(
        modifier = modifier
            .clip(shape)
            .background(Brush.verticalGradient(listOf(SurfaceWarm, SurfaceRaised)))
            .border(1.dp, borderColor, shape),
        content = content
    )
}

val PanelHighlight: Color = HighlightEdge
