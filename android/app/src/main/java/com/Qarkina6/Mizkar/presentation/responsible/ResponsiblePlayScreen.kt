package com.Qarkina6.Mizkar.presentation.responsible

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.Qarkina6.Mizkar.core.config.GameConfig
import com.Qarkina6.Mizkar.core.di.AppViewModelFactory
import com.Qarkina6.Mizkar.core.format.formatClock
import com.Qarkina6.Mizkar.core.theme.BackgroundLacquer
import com.Qarkina6.Mizkar.core.theme.BrassGold
import com.Qarkina6.Mizkar.core.theme.LightGold
import com.Qarkina6.Mizkar.core.theme.RoosterRed
import com.Qarkina6.Mizkar.core.theme.SurfaceDeep
import com.Qarkina6.Mizkar.core.theme.SurfaceWarm
import com.Qarkina6.Mizkar.core.theme.TextMuted
import com.Qarkina6.Mizkar.core.theme.TextSecondary
import com.Qarkina6.Mizkar.core.ui.GlassPanel
import com.Qarkina6.Mizkar.core.ui.ScreenScaffold
import com.Qarkina6.Mizkar.core.ui.SecondaryButton
import com.Qarkina6.Mizkar.core.ui.TopBarRow
import com.Qarkina6.Mizkar.presentation.dialog.ConfirmResetDialog

@Composable
fun ResponsiblePlayScreen(
    onMenu: () -> Unit,
    onBreakTaken: () -> Unit
) {
    val viewModel: ResponsiblePlayViewModel = viewModel(factory = AppViewModelFactory.Instance)
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ResponsiblePlayContent(
        state = state,
        onMenu = onMenu,
        onReminder = viewModel::setReminderMinutes,
        onBreak = {
            viewModel.takeBreak()
            onBreakTaken()
        },
        onReset = viewModel::askReset
    )

    if (state.showResetDialog) {
        ConfirmResetDialog(
            onConfirm = viewModel::confirmReset,
            onCancel = viewModel::cancelReset
        )
    }
    if (state.showReminder) {
        ReminderDialog(onClose = viewModel::dismissReminder)
    }
}

@Composable
private fun ResponsiblePlayContent(
    state: ResponsiblePlayUiState,
    onMenu: () -> Unit,
    onReminder: (Int) -> Unit,
    onBreak: () -> Unit,
    onReset: () -> Unit
) {
    ScreenScaffold(
        overlay = listOf(
            BackgroundLacquer.copy(alpha = 0.95f),
            BackgroundLacquer.copy(alpha = 0.97f),
            BackgroundLacquer.copy(alpha = 0.98f)
        )
    ) {
        TopBarRow(
            title = stringResource(R.string.responsible_title),
            menuLabel = stringResource(R.string.nav_menu),
            onMenu = onMenu
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceWarm)
                    .border(1.dp, RoosterRed.copy(alpha = 0.55f), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_info),
                        contentDescription = null,
                        tint = RoosterRed,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(9.dp))
                    Text(
                        text = stringResource(R.string.responsible_heading),
                        fontSize = 17.sp,
                        lineHeight = 22.sp,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = LightGold
                    )
                }
                Text(
                    text = stringResource(R.string.responsible_body),
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 9.dp)
                )
            }

            FactRow(R.string.responsible_fact_1)
            FactRow(R.string.responsible_fact_2)
            FactRow(R.string.responsible_fact_3)
            FactRow(R.string.responsible_fact_4)

            GlassPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = stringResource(R.string.responsible_session, formatClock(state.sessionSeconds)),
                        fontSize = 19.sp,
                        lineHeight = 24.sp,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold,
                        color = LightGold,
                        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                    )
                    Text(
                        text = stringResource(R.string.responsible_spins, state.spinsTotal),
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.responsible_reminder),
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ReminderChoice(
                                minutes = GameConfig.SESSION_REMINDER_SHORT_MIN,
                                selected = state.reminderMinutes == GameConfig.SESSION_REMINDER_SHORT_MIN,
                                onPick = onReminder
                            )
                            ReminderChoice(
                                minutes = GameConfig.SESSION_REMINDER_LONG_MIN,
                                selected = state.reminderMinutes == GameConfig.SESSION_REMINDER_LONG_MIN,
                                onPick = onReminder
                            )
                        }
                    }
                }
            }

            SecondaryButton(
                text = stringResource(R.string.responsible_break),
                onClick = onBreak,
                leadingIcon = R.drawable.ic_check,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )
            SecondaryButton(
                text = stringResource(R.string.responsible_reset),
                onClick = onReset,
                leadingIcon = R.drawable.ic_info,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            )
            Text(
                text = stringResource(R.string.responsible_footer),
                fontSize = 10.sp,
                lineHeight = 15.sp,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp, bottom = 18.dp)
            )
        }
    }
}

@Composable
private fun FactRow(textRes: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_check),
            contentDescription = null,
            tint = BrassGold,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = stringResource(textRes),
            fontSize = 12.sp,
            lineHeight = 17.sp,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary
        )
    }
}

@Composable
private fun ReminderChoice(
    minutes: Int,
    selected: Boolean,
    onPick: (Int) -> Unit
) {
    val label = stringResource(R.string.responsible_minutes, minutes)
    val stateLabel = if (selected) {
        stringResource(R.string.setting_state_on)
    } else {
        stringResource(R.string.setting_state_off)
    }
    SecondaryButton(
        text = label,
        onClick = { onPick(minutes) },
        height = 48.dp,
        stateLabel = stateLabel,
        modifier = Modifier
            .width(86.dp)
            .semantics { stateDescription = stateLabel }
    )
}

@Composable
private fun ReminderDialog(onClose: () -> Unit) {
    AlertDialog(
        onDismissRequest = onClose,
        containerColor = SurfaceDeep,
        titleContentColor = LightGold,
        textContentColor = TextSecondary,
        title = {
            Text(
                text = stringResource(R.string.reminder_title),
                fontSize = 17.sp,
                lineHeight = 23.sp,
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.ExtraBold
            )
        },
        text = {
            Text(
                text = stringResource(R.string.reminder_body),
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
