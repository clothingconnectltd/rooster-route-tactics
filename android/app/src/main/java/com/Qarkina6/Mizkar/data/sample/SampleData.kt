package com.Qarkina6.Mizkar.data.sample

import com.Qarkina6.Mizkar.R
import com.Qarkina6.Mizkar.domain.model.CardSet
import com.Qarkina6.Mizkar.domain.model.CollectionCard
import com.Qarkina6.Mizkar.domain.model.LeaderboardEntry
import com.Qarkina6.Mizkar.domain.model.Mission
import com.Qarkina6.Mizkar.domain.model.RewardKind
import com.Qarkina6.Mizkar.domain.model.RouteDefinition
import com.Qarkina6.Mizkar.domain.model.VisualReward
import com.Qarkina6.Mizkar.domain.usecase.MissionIds

object SampleData {

    const val SET_FARMYARD = "set_farmyard"
    const val SET_ROAD_SIGNS = "set_road_signs"
    const val SET_GOLDEN_TROVE = "set_golden_trove"

    const val REWARD_BRASS_BEZEL = "reward_brass_bezel"
    const val REWARD_BARN_BEZEL = "reward_barn_bezel"
    const val REWARD_MIDNIGHT_BEZEL = "reward_midnight_bezel"
    const val REWARD_DAWN_TINT = "reward_dawn_tint"
    const val REWARD_DUSK_TINT = "reward_dusk_tint"
    const val REWARD_AVATAR = "reward_avatar"

    val routes: List<RouteDefinition> = listOf(
        RouteDefinition(0, R.string.route_1, R.string.numeral_1, 10),
        RouteDefinition(1, R.string.route_2, R.string.numeral_2, 14),
        RouteDefinition(2, R.string.route_3, R.string.numeral_3, 18),
        RouteDefinition(3, R.string.route_4, R.string.numeral_4, 22),
        RouteDefinition(4, R.string.route_5, R.string.numeral_5, 26),
        RouteDefinition(5, R.string.route_6, R.string.numeral_6, 30)
    )

    val cardSets: List<CardSet> = listOf(
        CardSet(SET_FARMYARD, R.string.set_farmyard, REWARD_BARN_BEZEL),
        CardSet(SET_ROAD_SIGNS, R.string.set_road_signs, REWARD_DAWN_TINT),
        CardSet(SET_GOLDEN_TROVE, R.string.set_golden_trove, REWARD_MIDNIGHT_BEZEL)
    )

    val cards: List<CollectionCard> = listOf(
        CollectionCard("card_prize_hen", R.string.card_prize_hen, SET_FARMYARD, R.drawable.sprite_3),
        CollectionCard("card_golden_egg", R.string.card_golden_egg, SET_FARMYARD, R.drawable.sprite_4),
        CollectionCard("card_feed_crate", R.string.card_feed_crate, SET_FARMYARD, R.drawable.sprite_5),
        CollectionCard("card_down_feather", R.string.card_down_feather, SET_FARMYARD, R.drawable.ic_sym_feather),
        CollectionCard("card_route_sign", R.string.card_route_sign, SET_ROAD_SIGNS, R.drawable.sprite_2),
        CollectionCard("card_road_boot", R.string.card_road_boot, SET_ROAD_SIGNS, R.drawable.ic_sym_boot),
        CollectionCard("card_milestone", R.string.card_milestone, SET_ROAD_SIGNS, R.drawable.ic_card_milestone),
        CollectionCard("card_lantern", R.string.card_lantern, SET_ROAD_SIGNS, R.drawable.ic_card_lantern),
        CollectionCard("card_golden_rooster", R.string.card_golden_rooster, SET_GOLDEN_TROVE, R.drawable.sprite_1),
        CollectionCard("card_weathervane", R.string.card_weathervane, SET_GOLDEN_TROVE, R.drawable.ic_card_weathervane),
        CollectionCard("card_barn_key", R.string.card_barn_key, SET_GOLDEN_TROVE, R.drawable.ic_card_key),
        CollectionCard("card_brass_cup", R.string.card_brass_cup, SET_GOLDEN_TROVE, R.drawable.ic_trophy)
    )

    val missions: List<Mission> = listOf(
        Mission(MissionIds.SPIN_20, R.string.mission_spin, R.drawable.ic_refresh, 20, 300L),
        Mission(MissionIds.WIN_5, R.string.mission_win, R.drawable.ic_star, 5, 400L),
        Mission(MissionIds.BONUS_1, R.string.mission_bonus, R.drawable.ic_gift, 1, 600L),
        Mission(MissionIds.CARDS_2, R.string.mission_cards, R.drawable.ic_cards, 2, 500L),
        Mission(MissionIds.RUN_1, R.string.mission_run, R.drawable.ic_flag, 1, 350L)
    )

    val rewards: List<VisualReward> = listOf(
        VisualReward(REWARD_BRASS_BEZEL, R.string.reward_brass_bezel, R.string.reward_condition_start, RewardKind.BEZEL),
        VisualReward(REWARD_BARN_BEZEL, R.string.reward_barn_bezel, R.string.reward_condition_farmyard, RewardKind.BEZEL),
        VisualReward(REWARD_MIDNIGHT_BEZEL, R.string.reward_midnight_bezel, R.string.reward_condition_trove, RewardKind.BEZEL),
        VisualReward(REWARD_DAWN_TINT, R.string.reward_dawn_tint, R.string.reward_condition_signs, RewardKind.TINT),
        VisualReward(REWARD_DUSK_TINT, R.string.reward_dusk_tint, R.string.reward_condition_route, RewardKind.TINT),
        VisualReward(REWARD_AVATAR, R.string.reward_avatar, R.string.reward_condition_daily, RewardKind.AVATAR)
    )

    val rivals: List<LeaderboardEntry> = listOf(
        LeaderboardEntry(R.string.rival_1, 18400L, false),
        LeaderboardEntry(R.string.rival_2, 15900L, false),
        LeaderboardEntry(R.string.rival_3, 14250L, false),
        LeaderboardEntry(R.string.rival_4, 12700L, false),
        LeaderboardEntry(R.string.rival_5, 11050L, false),
        LeaderboardEntry(R.string.rival_6, 9400L, false),
        LeaderboardEntry(R.string.rival_7, 7800L, false),
        LeaderboardEntry(R.string.rival_8, 6250L, false)
    )

    val dailyLadder: List<Long> = listOf(250L, 400L, 600L, 900L, 1200L, 1600L, 2500L)
}
