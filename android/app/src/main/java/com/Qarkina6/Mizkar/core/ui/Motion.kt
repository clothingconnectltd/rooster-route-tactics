package com.Qarkina6.Mizkar.core.ui
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
@Composable
fun InfiniteTransition.animateLoop(
    durationMs: Int,
    from: Float,
    to: Float,
    label: String,
    mode: RepeatMode = RepeatMode.Restart
): State<Float> {
    val spec: InfiniteRepeatableSpec<Float> = infiniteRepeatable(
        animation = tween(durationMillis = durationMs, easing = LinearEasing),
        repeatMode = mode
    )
    return animateFloat(
        initialValue = from,
        targetValue = to,
        animationSpec = spec,
        label = label
    )
}
