package com.Qarkina6.Mizkar.presentation.splash

import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Qarkina6.Mizkar.R
import com.Qarkina6.Mizkar.core.di.AppViewModelFactory
import com.Qarkina6.Mizkar.core.theme.BackgroundLacquer
import com.Qarkina6.Mizkar.core.theme.BrassGold
import com.Qarkina6.Mizkar.core.theme.LightGold
import com.Qarkina6.Mizkar.core.theme.RoosterRed
import com.Qarkina6.Mizkar.core.theme.SurfaceRaised
import com.Qarkina6.Mizkar.core.theme.TextMuted
import com.Qarkina6.Mizkar.core.ui.ScreenScaffold
import com.Qarkina6.Mizkar.core.ui.animateLoop

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val viewModel: SplashViewModel = viewModel(factory = AppViewModelFactory.Instance)
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.finished) {
        if (state.finished) onFinished()
    }

    SplashContent(reducedMotion = state.reducedMotion)
}

@Composable
private fun SplashContent(reducedMotion: Boolean) {
    val transition = rememberInfiniteTransition(label = "splashLoop")
    val stripeShift by transition.animateLoop(2600, 0f, 1f, "stripes")
    val ringSweep by transition.animateLoop(9000, 0f, 360f, "ring")
    val shift = if (reducedMotion) 0f else stripeShift
    val sweep = if (reducedMotion) 0f else ringSweep

    ScreenScaffold(
        overlay = listOf(
            BackgroundLacquer.copy(alpha = 0.95f),
            BackgroundLacquer.copy(alpha = 0.88f),
            BackgroundLacquer.copy(alpha = 0.97f)
        ),
        decor = { RoadStripes(shift) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(260.dp)
                        .background(
                            Brush.radialGradient(
                                listOf(BrassGold.copy(alpha = 0.12f), Color.Transparent)
                            ),
                            CircleShape
                        )
                )
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .graphicsLayer { rotationZ = sweep }
                        .border(2.dp, BrassGold.copy(alpha = 0.55f), CircleShape)
                )
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = stringResource(R.string.cd_logo),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(150.dp)
                )
            }
            Text(
                text = stringResource(R.string.splash_title),
                fontSize = 34.sp,
                lineHeight = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 3.sp,
                color = LightGold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 26.dp)
            )
            Text(
                text = stringResource(R.string.splash_subtitle),
                fontSize = 11.sp,
                lineHeight = 15.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 10.dp)
            )
            LinearProgressIndicator(
                color = BrassGold,
                trackColor = SurfaceRaised,
                modifier = Modifier
                    .padding(top = 34.dp)
                    .width(180.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
            )
            Text(
                text = stringResource(R.string.splash_loading),
                fontSize = 10.sp,
                lineHeight = 14.sp,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextMuted,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 36.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.splash_badge),
                fontSize = 10.sp,
                lineHeight = 14.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold,
                color = RoosterRed,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, RoosterRed.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 16.dp, vertical = 7.dp)
            )
        }
    }
}

@Composable
private fun RoadStripes(shift: Float) {
    Box(modifier = Modifier.fillMaxSize()) {
        StripeRow(shift = shift, topFraction = 0.64f, offsetSeed = 0f)
        StripeRow(shift = shift, topFraction = 0.72f, offsetSeed = 0.33f)
        StripeRow(shift = shift, topFraction = 0.80f, offsetSeed = 0.66f)
    }
}

@Composable
private fun StripeRow(shift: Float, topFraction: Float, offsetSeed: Float) {
    val progress = (shift + offsetSeed) % 1f
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(topFraction)
            .padding(horizontal = 0.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = (progress * 320f).dp)
                .width(64.dp)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(BrassGold.copy(alpha = 0.14f))
        )
    }
}
