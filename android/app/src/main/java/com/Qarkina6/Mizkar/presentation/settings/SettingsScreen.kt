package com.Qarkina6.Mizkar.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
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
import com.Qarkina6.Mizkar.core.theme.TextSecondary
import com.Qarkina6.Mizkar.core.ui.GlassPanel
import com.Qarkina6.Mizkar.core.ui.ScreenScaffold
import com.Qarkina6.Mizkar.core.ui.SecondaryButton
import com.Qarkina6.Mizkar.core.ui.TopBarRow

@Composable
fun SettingsScreen(
    onMenu: () -> Unit,
    onResponsible: () -> Unit
) {
    val viewModel: SettingsViewModel = viewModel(factory = AppViewModelFactory.Instance)
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsContent(
        state = state,
        onMenu = onMenu,
        onResponsible = onResponsible,
        onFastReels = viewModel::setFastReels,
        onNearWin = viewModel::setNearWinHighlight,
        onReducedMotion = viewModel::setReducedMotion,
        onRouteMeter = viewModel::setShowRouteMeter,
        onName = viewModel::setLeaderboardName,
        onRetry = viewModel::retrySave
    )
}

@Composable
private fun SettingsContent(
    state: SettingsUiState,
    onMenu: () -> Unit,
    onResponsible: () -> Unit,
    onFastReels: (Boolean) -> Unit,
    onNearWin: (Boolean) -> Unit,
    onReducedMotion: (Boolean) -> Unit,
    onRouteMeter: (Boolean) -> Unit,
    onName: (String) -> Unit,
    onRetry: () -> Unit
) {
    ScreenScaffold(
        overlay = listOf(
            BackgroundLacquer.copy(alpha = 0.92f),
            SurfaceRaised.copy(alpha = 0.84f),
            BackgroundLacquer.copy(alpha = 0.96f)
        )
    ) {
        TopBarRow(
            title = stringResource(R.string.settings_title),
            menuLabel = stringResource(R.string.nav_menu),
            onMenu = onMenu
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SpeedRow(fast = state.fastReels, onFastReels = onFastReels)
            SwitchRow(
                titleRes = R.string.setting_near_win,
                descriptionRes = R.string.setting_near_win_desc,
                checked = state.nearWinHighlight,
                onChange = onNearWin
            )
            SwitchRow(
                titleRes = R.string.setting_reduced_motion,
                descriptionRes = R.string.setting_reduced_motion_desc,
                checked = state.reducedMotion,
                onChange = onReducedMotion
            )
            SwitchRow(
                titleRes = R.string.setting_route_meter,
                descriptionRes = R.string.setting_route_meter_desc,
                checked = state.showRouteMeter,
                onChange = onRouteMeter
            )
            NameRow(value = state.leaderboardName, onName = onName)
            StorageRow(storageOk = state.storageOk, onRetry = onRetry)
            SecondaryButton(
                text = stringResource(R.string.nav_responsible),
                onClick = onResponsible,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp)
            )
        }
    }
}

@Composable
private fun SpeedRow(fast: Boolean, onFastReels: (Boolean) -> Unit) {
    val normalLabel = stringResource(R.string.setting_speed_normal)
    val fastLabel = stringResource(R.string.setting_speed_fast)
    GlassPanel(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LabelColumn(
                titleRes = R.string.setting_speed,
                descriptionRes = R.string.setting_speed_desc,
                modifier = Modifier.weight(1f)
            )
            SecondaryButton(
                text = normalLabel,
                onClick = { onFastReels(false) },
                enabled = fast,
                height = 48.dp,
                stateLabel = if (!fast) normalLabel else fastLabel,
                modifier = Modifier.width(92.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            SecondaryButton(
                text = fastLabel,
                onClick = { onFastReels(true) },
                enabled = !fast,
                height = 48.dp,
                modifier = Modifier.width(82.dp)
            )
        }
    }
}

@Composable
private fun SwitchRow(
    titleRes: Int,
    descriptionRes: Int,
    checked: Boolean,
    onChange: (Boolean) -> Unit
) {
    val stateLabel = if (checked) {
        stringResource(R.string.setting_state_on)
    } else {
        stringResource(R.string.setting_state_off)
    }
    GlassPanel(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LabelColumn(
                titleRes = titleRes,
                descriptionRes = descriptionRes,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = checked,
                onCheckedChange = onChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = BackgroundLacquer,
                    checkedTrackColor = BrassGold,
                    uncheckedThumbColor = TextMuted,
                    uncheckedTrackColor = SurfaceRaised
                ),
                modifier = Modifier
                    .height(48.dp)
                    .semantics { stateDescription = stateLabel }
            )
        }
    }
}

@Composable
private fun NameRow(value: String, onName: (String) -> Unit) {
    GlassPanel(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            LabelColumn(
                titleRes = R.string.setting_name,
                descriptionRes = R.string.setting_name_desc,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = value,
                onValueChange = onName,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun StorageRow(storageOk: Boolean, onRetry: () -> Unit) {
    GlassPanel(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.settings_storage),
                    fontSize = 14.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Text(
                    text = if (storageOk) {
                        stringResource(R.string.settings_storage_ok)
                    } else {
                        stringResource(R.string.settings_storage_failed)
                    },
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = if (storageOk) TextMuted else RoosterRed,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Text(
                    text = stringResource(R.string.settings_version) + " " +
                        stringResource(R.string.settings_version_value),
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    letterSpacing = 1.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            if (!storageOk) {
                SecondaryButton(
                    text = stringResource(R.string.settings_retry),
                    onClick = onRetry,
                    height = 48.dp,
                    modifier = Modifier.width(96.dp)
                )
            }
        }
    }
}

@Composable
private fun LabelColumn(
    titleRes: Int,
    descriptionRes: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(titleRes),
            fontSize = 14.sp,
            lineHeight = 19.sp,
            fontWeight = FontWeight.Bold,
            color = LightGold
        )
        Text(
            text = stringResource(descriptionRes),
            fontSize = 11.sp,
            lineHeight = 15.sp,
            color = TextMuted,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
