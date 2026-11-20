package com.Qarkina6.Mizkar.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Qarkina6.Mizkar.core.theme.BrassGold
import com.Qarkina6.Mizkar.core.theme.HairlineBorder
import com.Qarkina6.Mizkar.core.theme.LightGold
import com.Qarkina6.Mizkar.core.theme.SurfaceRaised
import com.Qarkina6.Mizkar.core.theme.TextMuted
import com.Qarkina6.Mizkar.core.theme.TextSecondary

@Composable
fun StatCard(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    GlassPanel(
        modifier = modifier,
        radius = 12.dp,
        borderColor = accent.copy(alpha = 0.35f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(accent)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = value,
                    fontSize = 17.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = accent
                )
                Text(
                    text = label,
                    fontSize = 10.sp,
                    lineHeight = 13.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
fun ProgressMeter(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 6.dp,
    accent: Color = BrassGold
) {
    val clamped = progress.coerceIn(0f, 1f)
    Box(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(height / 2))
            .background(SurfaceRaised)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(clamped)
                .fillMaxHeight()
                .background(Brush.horizontalGradient(listOf(accent, LightGold)))
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    trailing: String? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 11.sp,
            lineHeight = 14.sp,
            letterSpacing = 2.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextMuted
        )
        if (trailing != null) {
            Text(
                text = trailing,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold,
                color = LightGold
            )
        }
    }
}

@Composable
fun EmptyState(
    icon: Int,
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(44.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = message,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun RewardPill(
    label: String,
    amount: String,
    claimed: Boolean,
    current: Boolean,
    modifier: Modifier = Modifier
) {
    val border = when {
        current -> LightGold
        claimed -> BrassGold.copy(alpha = 0.6f)
        else -> HairlineBorder
    }
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (claimed) BrassGold.copy(alpha = 0.22f) else SurfaceRaised)
            .border(if (current) 2.dp else 1.dp, border, RoundedCornerShape(10.dp))
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            lineHeight = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (claimed || current) LightGold else TextMuted
        )
        Text(
            text = amount,
            fontSize = 11.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (claimed || current) LightGold else TextSecondary
        )
    }
}
