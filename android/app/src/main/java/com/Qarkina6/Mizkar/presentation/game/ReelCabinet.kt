package com.Qarkina6.Mizkar.presentation.game

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.Qarkina6.Mizkar.core.config.GameConfig
import com.Qarkina6.Mizkar.core.theme.BrassGold
import com.Qarkina6.Mizkar.core.theme.DeepBrass
import com.Qarkina6.Mizkar.core.theme.HairlineBorder
import com.Qarkina6.Mizkar.core.theme.LightGold
import com.Qarkina6.Mizkar.core.theme.RoosterRed
import com.Qarkina6.Mizkar.core.theme.SurfaceDeep
import com.Qarkina6.Mizkar.core.ui.CabinetPanel
import com.Qarkina6.Mizkar.core.ui.animateLoop
import com.Qarkina6.Mizkar.data.sample.SampleData
import com.Qarkina6.Mizkar.domain.model.SlotSymbol
import com.Qarkina6.Mizkar.domain.usecase.PAY_LINES

const val CABINET_FRAME_DP = 12f
const val CABINET_MAX_DP = 352f

@Composable
fun ReelCabinet(
    state: GameUiState,
    cellSize: Float,
    reducedMotion: Boolean,
    modifier: Modifier = Modifier
) {
    val cell = cellSize.dp
    val cabinetWidth = (cellSize * GameConfig.REELS + CABINET_FRAME_DP * 2).dp
    val cabinetHeight = (cellSize * GameConfig.ROWS + CABINET_FRAME_DP * 2).dp
    val transition = rememberInfiniteTransition(label = "cabinetLoop")
    val pulseRaw by transition.animateLoop(520, 0.3f, 1f, "nearWinPulse", RepeatMode.Reverse)
    val pulse = if (reducedMotion) 0.7f else pulseRaw

    CabinetPanel(
        modifier = modifier
            .width(cabinetWidth)
            .height(cabinetHeight),
        bezel = bezelBrush(state.bezelId)
    ) {
        Box(modifier = Modifier.padding(CABINET_FRAME_DP.dp)) {
            Column {
                for (row in 0 until GameConfig.ROWS) {
                    Row {
                        for (reel in 0 until GameConfig.REELS) {
                            val index = row * GameConfig.REELS + reel
                            ReelCell(
                                symbol = state.grid[row][reel],
                                cell = cell,
                                highlighted = index in state.winningCells,
                                nearWin = state.nearWinReel == reel,
                                pulse = pulse,
                                blurred = state.spinningReels.getOrElse(reel) { false }
                            )
                        }
                    }
                }
            }
            if (state.winningLines.isNotEmpty()) {
                PayLineOverlay(
                    lines = state.winningLines,
                    modifier = Modifier.matchParentSize()
                )
            }
        }
    }
}

@Composable
private fun ReelCell(
    symbol: SlotSymbol,
    cell: Dp,
    highlighted: Boolean,
    nearWin: Boolean,
    pulse: Float,
    blurred: Boolean
) {
    val borderColor = when {
        highlighted -> LightGold.copy(alpha = 0.85f)
        nearWin -> RoosterRed.copy(alpha = pulse)
        else -> HairlineBorder
    }
    Box(
        modifier = Modifier
            .size(cell)
            .padding(1.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(SurfaceDeep.copy(alpha = if (highlighted) 0.75f else 0.92f))
            .border(if (highlighted || nearWin) 2.dp else 1.dp, borderColor, RoundedCornerShape(6.dp)),
        contentAlignment = Alignment.Center
    ) {
        SymbolTile(
            symbol = symbol,
            size = cell - 16.dp,
            dimmed = blurred
        )
    }
}

@Composable
fun SymbolTile(
    symbol: SlotSymbol,
    size: Dp,
    dimmed: Boolean = false,
    modifier: Modifier = Modifier
) {
    val description = stringResource(descriptionOf(symbol))
    if (isVectorArt(symbol)) {
        Icon(
            painter = painterResource(artOf(symbol)),
            contentDescription = description,
            tint = if (dimmed) BrassGold.copy(alpha = 0.55f) else LightGold,
            modifier = modifier.size(size)
        )
    } else {
        Image(
            painter = painterResource(artOf(symbol)),
            contentDescription = description,
            contentScale = ContentScale.Fit,
            modifier = modifier.size(size)
        )
    }
}

@Composable
private fun PayLineOverlay(lines: List<Int>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val cellWidth = size.width / GameConfig.REELS
        val cellHeight = size.height / GameConfig.ROWS
        lines.forEach { lineIndex ->
            val pattern = PAY_LINES.getOrNull(lineIndex) ?: return@forEach
            val points = pattern.mapIndexed { reel, row ->
                Offset(
                    x = cellWidth * reel + cellWidth / 2f,
                    y = cellHeight * row + cellHeight / 2f
                )
            }
            for (index in 0 until points.size - 1) {
                drawLine(
                    color = LightGold.copy(alpha = 0.75f),
                    start = points[index],
                    end = points[index + 1],
                    strokeWidth = 5f,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

fun bezelBrush(bezelId: String): Brush = when (bezelId) {
    SampleData.REWARD_BARN_BEZEL -> Brush.linearGradient(
        listOf(Color(0xFF8A6A3C), Color(0xFF53391D))
    )
    SampleData.REWARD_MIDNIGHT_BEZEL -> Brush.linearGradient(
        listOf(Color(0xFF4C5A80), Color(0xFF1B2136))
    )
    else -> Brush.linearGradient(listOf(LightGold, BrassGold, DeepBrass))
}
