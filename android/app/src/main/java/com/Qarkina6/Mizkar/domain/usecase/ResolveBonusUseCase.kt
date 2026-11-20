package com.Qarkina6.Mizkar.domain.usecase

import com.Qarkina6.Mizkar.core.config.GameConfig
import com.Qarkina6.Mizkar.domain.model.BonusTile
import com.Qarkina6.Mizkar.domain.model.BonusTileKind
import kotlin.random.Random

class ResolveBonusUseCase {

    operator fun invoke(bet: Long, seed: Long): List<BonusTile> {
        val tiles = mutableListOf(
            BonusTile(BonusTileKind.CREDITS, bet * 2),
            BonusTile(BonusTileKind.CREDITS, bet * 5),
            BonusTile(BonusTileKind.CREDITS, bet * 15),
            BonusTile(BonusTileKind.STEP, 0L),
            BonusTile(BonusTileKind.STEP, 0L),
            BonusTile(BonusTileKind.STOP, 0L)
        )
        return tiles.shuffled(Random(seed))
    }

    fun guaranteedMinimum(bet: Long): Long = bet * GameConfig.BONUS_MIN_MULTIPLIER
}
