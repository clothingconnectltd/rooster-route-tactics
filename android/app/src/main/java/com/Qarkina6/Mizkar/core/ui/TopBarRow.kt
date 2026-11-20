package com.Qarkina6.Mizkar.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Qarkina6.Mizkar.R
import com.Qarkina6.Mizkar.core.theme.LightGold
import com.Qarkina6.Mizkar.core.theme.TextSecondary

@Composable
fun TopBarRow(
    title: String,
    menuLabel: String,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier,
    balance: String? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextButton(onClick = onMenu, modifier = Modifier.height(48.dp)) {
            Image(
                painter = painterResource(R.drawable.icon_home),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = menuLabel,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = TextSecondary
            )
        }
        Text(
            text = title,
            fontSize = 15.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            color = LightGold
        )
        Box(modifier = Modifier.width(96.dp), contentAlignment = Alignment.CenterEnd) {
            if (balance != null) {
                BrassPill(modifier = Modifier.height(32.dp)) {
                    Text(
                        text = balance,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = LightGold,
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .align(Alignment.Center)
                            .semantics { liveRegion = LiveRegionMode.Polite }
                    )
                }
            }
        }
    }
}
