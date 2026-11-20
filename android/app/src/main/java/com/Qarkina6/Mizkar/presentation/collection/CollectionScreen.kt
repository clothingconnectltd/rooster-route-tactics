package com.Qarkina6.Mizkar.presentation.collection

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Qarkina6.Mizkar.R
import com.Qarkina6.Mizkar.core.di.AppViewModelFactory
import com.Qarkina6.Mizkar.core.format.formatCredits
import com.Qarkina6.Mizkar.core.theme.BackgroundLacquer
import com.Qarkina6.Mizkar.core.theme.BrassGold
import com.Qarkina6.Mizkar.core.theme.HairlineBorder
import com.Qarkina6.Mizkar.core.theme.LightGold
import com.Qarkina6.Mizkar.core.theme.SurfaceDeep
import com.Qarkina6.Mizkar.core.theme.SurfaceRaised
import com.Qarkina6.Mizkar.core.theme.TextMuted
import com.Qarkina6.Mizkar.core.theme.TextSecondary
import com.Qarkina6.Mizkar.core.ui.EmptyState
import com.Qarkina6.Mizkar.core.ui.GlassPanel
import com.Qarkina6.Mizkar.core.ui.ProgressMeter
import com.Qarkina6.Mizkar.core.ui.RewardPill
import com.Qarkina6.Mizkar.core.ui.ScreenScaffold
import com.Qarkina6.Mizkar.core.ui.SecondaryButton
import com.Qarkina6.Mizkar.core.ui.SectionHeader
import com.Qarkina6.Mizkar.core.ui.TopBarRow

@Composable
fun CollectionScreen(onMenu: () -> Unit) {
    val viewModel: CollectionViewModel = viewModel(factory = AppViewModelFactory.Instance)
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    CollectionContent(
        state = state,
        onMenu = onMenu,
        onSelectTab = viewModel::selectTab,
        onClaimMission = viewModel::claim,
        onClaimDaily = viewModel::claimDaily,
        onEquip = viewModel::equip
    )
}

@Composable
private fun CollectionContent(
    state: CollectionUiState,
    onMenu: () -> Unit,
    onSelectTab: (Int) -> Unit,
    onClaimMission: (String) -> Unit,
    onClaimDaily: () -> Unit,
    onEquip: (String) -> Unit
) {
    ScreenScaffold(
        overlay = listOf(
            BackgroundLacquer.copy(alpha = 0.90f),
            SurfaceDeep.copy(alpha = 0.86f),
            BackgroundLacquer.copy(alpha = 0.95f)
        )
    ) {
        TopBarRow(
            title = stringResource(R.string.collection_title),
            menuLabel = stringResource(R.string.nav_menu),
            onMenu = onMenu,
            balance = stringResource(R.string.credits_value, formatCredits(state.balance))
        )
        val titles = listOf(
            stringResource(R.string.tab_symbols),
            stringResource(R.string.tab_missions),
            stringResource(R.string.tab_rewards)
        )
        val selectedLabel = stringResource(R.string.tab_state_selected)
        SecondaryTabRow(
            selectedTabIndex = state.tab,
            containerColor = Color.Transparent,
            contentColor = LightGold
        ) {
            titles.forEachIndexed { index, title ->
                Tab(
                    selected = state.tab == index,
                    onClick = { onSelectTab(index) },
                    modifier = Modifier
                        .height(48.dp)
                        .semantics { if (state.tab == index) stateDescription = selectedLabel }
                ) {
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (state.tab == index) LightGold else TextMuted
                    )
                }
            }
        }

        when (state.tab) {
            1 -> MissionsTab(
                state = state,
                onClaimMission = onClaimMission,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            2 -> RewardsTab(
                state = state,
                onClaimDaily = onClaimDaily,
                onEquip = onEquip,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            else -> SymbolsTab(
                state = state,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}

@Composable
private fun SymbolsTab(state: CollectionUiState, modifier: Modifier = Modifier) {
    val collectedCount = state.cards.count { it.collected }
    if (collectedCount == 0) {
        Column(modifier = modifier, verticalArrangement = Arrangement.Center) {
            EmptyState(
                icon = R.drawable.ic_search,
                message = stringResource(R.string.symbols_empty)
            )
            SetSummary(state = state, modifier = Modifier.padding(horizontal = 16.dp))
        }
        return
    }
    Column(modifier = modifier) {
        SetSummary(state = state, modifier = Modifier.padding(horizontal = 16.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            gridItems(state.cards) { card ->
                SymbolCardView(card = card)
            }
        }
    }
}

@Composable
private fun SetSummary(state: CollectionUiState, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        state.sets.forEach { set ->
            Column(modifier = Modifier.padding(top = 8.dp)) {
                SectionHeader(
                    title = stringResource(set.nameRes),
                    trailing = stringResource(R.string.set_progress, set.collected, set.total)
                )
                ProgressMeter(
                    progress = set.collected.toFloat() / set.total.toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )
                if (set.collected == set.total) {
                    Text(
                        text = stringResource(R.string.set_bonus_active),
                        fontSize = 10.sp,
                        lineHeight = 14.sp,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold,
                        color = LightGold,
                        modifier = Modifier.padding(top = 3.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SymbolCardView(card: SymbolCardUi) {
    val shape = RoundedCornerShape(12.dp)
    Column(
        modifier = Modifier
            .aspectRatio(0.82f)
            .clip(shape)
            .background(SurfaceRaised)
            .border(
                1.dp,
                if (card.collected) BrassGold.copy(alpha = 0.6f) else HairlineBorder,
                shape
            )
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (card.collected) {
            Image(
                painter = painterResource(card.artRes),
                contentDescription = stringResource(card.nameRes),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(44.dp)
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = stringResource(card.nameRes),
                fontSize = 9.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        } else {
            Icon(
                painter = painterResource(R.drawable.ic_lock),
                contentDescription = stringResource(R.string.cd_locked_card),
                tint = TextMuted,
                modifier = Modifier
                    .size(30.dp)
                    .alpha(0.5f)
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = stringResource(R.string.card_locked),
                fontSize = 11.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun MissionsTab(
    state: CollectionUiState,
    onClaimMission: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val open = state.missions.filter { !it.claimed }
    if (open.isEmpty()) {
        Column(modifier = modifier, verticalArrangement = Arrangement.Center) {
            EmptyState(
                icon = R.drawable.ic_check,
                message = stringResource(R.string.missions_empty)
            )
        }
        return
    }
    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(vertical = 10.dp)
    ) {
        items(state.missions) { mission ->
            MissionRow(mission = mission, onClaim = onClaimMission)
        }
    }
}

@Composable
private fun MissionRow(mission: MissionUi, onClaim: (String) -> Unit) {
    val ready = mission.progress >= mission.target && !mission.claimed
    GlassPanel(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(mission.iconRes),
                contentDescription = null,
                tint = if (mission.claimed) TextMuted else BrassGold,
                modifier = Modifier.size(22.dp)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp)
            ) {
                Text(
                    text = stringResource(mission.nameRes),
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (mission.claimed) TextMuted else TextSecondary
                )
                Text(
                    text = stringResource(R.string.progress_fraction, mission.progress, mission.target),
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 2.dp)
                )
                ProgressMeter(
                    progress = mission.progress.toFloat() / mission.target.toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                )
            }
            SecondaryButton(
                text = if (mission.claimed) {
                    stringResource(R.string.action_equipped)
                } else {
                    stringResource(R.string.action_claim)
                },
                onClick = { onClaim(mission.id) },
                enabled = ready,
                height = 48.dp,
                modifier = Modifier.width(100.dp)
            )
        }
    }
}

@Composable
private fun RewardsTab(
    state: CollectionUiState,
    onClaimDaily: () -> Unit,
    onEquip: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        SectionHeader(
            title = stringResource(R.string.daily_ladder_header),
            modifier = Modifier.padding(top = 10.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            state.dailyPills.forEach { pill ->
                RewardPill(
                    label = stringResource(R.string.day_short, pill.day),
                    amount = formatCredits(pill.amount),
                    claimed = pill.claimed,
                    current = pill.current,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        SecondaryButton(
            text = stringResource(R.string.action_claim_amount, formatCredits(state.dailyAmount)),
            onClick = onClaimDaily,
            enabled = state.dailyReady,
            leadingIcon = R.drawable.ic_gift,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        val unlocked = state.rewards.count { it.unlocked }
        if (unlocked == 0) {
            EmptyState(
                icon = R.drawable.ic_lock,
                message = stringResource(R.string.rewards_empty)
            )
            return@Column
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            gridItems(state.rewards) { reward ->
                RewardCard(reward = reward, onEquip = onEquip)
            }
        }
    }
}

@Composable
private fun RewardCard(reward: RewardUi, onEquip: (String) -> Unit) {
    GlassPanel(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (reward.unlocked) BrassGold.copy(alpha = 0.45f) else HairlineBorder
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = stringResource(reward.nameRes),
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (reward.unlocked) LightGold else TextMuted
            )
            if (!reward.unlocked) {
                Row(
                    modifier = Modifier.padding(top = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_lock),
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = stringResource(reward.conditionRes),
                        fontSize = 9.sp,
                        lineHeight = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextMuted
                    )
                }
                Box(modifier = Modifier.height(48.dp))
            } else {
                SecondaryButton(
                    text = if (reward.equipped) {
                        stringResource(R.string.action_equipped)
                    } else {
                        stringResource(R.string.action_equip)
                    },
                    onClick = { onEquip(reward.id) },
                    enabled = !reward.equipped,
                    height = 48.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                )
            }
        }
    }
}
