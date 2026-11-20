package com.Qarkina6.Mizkar.presentation.game

import com.Qarkina6.Mizkar.R
import com.Qarkina6.Mizkar.domain.model.SlotSymbol

fun artOf(symbol: SlotSymbol): Int = when (symbol) {
    SlotSymbol.GOLDEN_ROOSTER -> R.drawable.sprite_1
    SlotSymbol.ROUTE_SIGN -> R.drawable.sprite_2
    SlotSymbol.PRIZE_HEN -> R.drawable.sprite_3
    SlotSymbol.GOLDEN_EGG -> R.drawable.sprite_4
    SlotSymbol.FEED_CRATE -> R.drawable.sprite_5
    SlotSymbol.ROAD_BOOT -> R.drawable.ic_sym_boot
    SlotSymbol.FEATHER -> R.drawable.ic_sym_feather
}

fun nameOf(symbol: SlotSymbol): Int = when (symbol) {
    SlotSymbol.GOLDEN_ROOSTER -> R.string.card_golden_rooster
    SlotSymbol.ROUTE_SIGN -> R.string.card_route_sign
    SlotSymbol.PRIZE_HEN -> R.string.card_prize_hen
    SlotSymbol.GOLDEN_EGG -> R.string.card_golden_egg
    SlotSymbol.FEED_CRATE -> R.string.card_feed_crate
    SlotSymbol.ROAD_BOOT -> R.string.card_road_boot
    SlotSymbol.FEATHER -> R.string.card_down_feather
}

fun descriptionOf(symbol: SlotSymbol): Int = when (symbol) {
    SlotSymbol.GOLDEN_ROOSTER -> R.string.cd_symbol_rooster
    SlotSymbol.ROUTE_SIGN -> R.string.cd_symbol_sign
    SlotSymbol.PRIZE_HEN -> R.string.cd_symbol_hen
    SlotSymbol.GOLDEN_EGG -> R.string.cd_symbol_egg
    SlotSymbol.FEED_CRATE -> R.string.cd_symbol_crate
    SlotSymbol.ROAD_BOOT -> R.string.cd_symbol_boot
    SlotSymbol.FEATHER -> R.string.cd_symbol_feather
}

fun isVectorArt(symbol: SlotSymbol): Boolean =
    symbol == SlotSymbol.ROAD_BOOT || symbol == SlotSymbol.FEATHER
