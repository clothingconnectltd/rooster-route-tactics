package com.Qarkina6.Mizkar.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = darkColorScheme(
    primary = BrassGold,
    onPrimary = BackgroundLacquer,
    primaryContainer = SurfaceWarm,
    onPrimaryContainer = LightGold,
    secondary = LightGold,
    onSecondary = BackgroundLacquer,
    secondaryContainer = SurfaceRaised,
    onSecondaryContainer = LightGold,
    tertiary = RoosterRed,
    onTertiary = TextPrimary,
    background = BackgroundLacquer,
    onBackground = TextPrimary,
    surface = SurfaceDeep,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceRaised,
    onSurfaceVariant = TextSecondary,
    outline = DeepBrass,
    outlineVariant = HairlineBorder,
    error = RoosterRed,
    onError = TextPrimary
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = AppTypography,
        content = content
    )
}
