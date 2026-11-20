package com.Qarkina6.Mizkar.presentation.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.Qarkina6.Mizkar.R
import com.Qarkina6.Mizkar.core.theme.BrassGold
import com.Qarkina6.Mizkar.core.theme.LightGold
import com.Qarkina6.Mizkar.core.theme.SurfaceDeep
import com.Qarkina6.Mizkar.core.theme.TextSecondary
import com.Qarkina6.Mizkar.core.ui.PrimaryButton
import com.Qarkina6.Mizkar.core.ui.SecondaryButton

@Composable
fun PauseDialog(
    onResume: () -> Unit,
    onResponsible: () -> Unit,
    onMenu: () -> Unit,
    onEndRun: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = true, dismissOnBackPress = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(SurfaceDeep)
                .border(1.dp, BrassGold.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.pause_title),
                fontSize = 20.sp,
                lineHeight = 26.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.ExtraBold,
                color = LightGold
            )
            PrimaryButton(
                text = stringResource(R.string.pause_resume),
                onClick = onResume,
                height = 54.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp)
            )
            SecondaryButton(
                text = stringResource(R.string.pause_end_run),
                onClick = onEndRun,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            )
            SecondaryButton(
                text = stringResource(R.string.nav_responsible),
                onClick = onResponsible,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            )
            SecondaryButton(
                text = stringResource(R.string.nav_menu),
                onClick = onMenu,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            )
        }
    }
}

@Composable
fun HowToPlayDialog(onClose: () -> Unit) {
    AlertDialog(
        onDismissRequest = onClose,
        containerColor = SurfaceDeep,
        titleContentColor = LightGold,
        textContentColor = TextSecondary,
        title = {
            Text(
                text = stringResource(R.string.how_to_title),
                fontSize = 18.sp,
                lineHeight = 24.sp,
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.ExtraBold
            )
        },
        text = {
            Text(
                text = stringResource(R.string.how_to_body),
                fontSize = 13.sp,
                lineHeight = 19.sp
            )
        },
        confirmButton = {
            TextButton(onClick = onClose) {
                Text(
                    text = stringResource(R.string.how_to_close),
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrassGold
                )
            }
        }
    )
}

@Composable
fun ConfirmResetDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        containerColor = SurfaceDeep,
        titleContentColor = LightGold,
        textContentColor = TextSecondary,
        title = {
            Text(
                text = stringResource(R.string.reset_title),
                fontSize = 18.sp,
                lineHeight = 24.sp,
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.ExtraBold
            )
        },
        text = {
            Text(
                text = stringResource(R.string.reset_body),
                fontSize = 13.sp,
                lineHeight = 19.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(R.string.reset_confirm),
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = BrassGold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(
                    text = stringResource(R.string.reset_cancel),
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            }
        }
    )
}
