package com.Qarkina6.Mizkar.domain.usecase

import com.Qarkina6.Mizkar.core.config.GameConfig
import com.Qarkina6.Mizkar.domain.model.SlotSymbol
import com.Qarkina6.Mizkar.domain.model.SpinOutcome
import com.Qarkina6.Mizkar.domain.model.SpinRequest
import kotlin.random.Random

class SpinReelsUseCase(private val evaluateLines: EvaluateLinesUseCase) {

    operator fun invoke(request: SpinRequest): SpinOutcome {
        val random = Random(request.seed)
        val weights = buildWeights(request)
        val total = weights.values.sum()

        val grid = MutableList(GameConfig.ROWS) { MutableList(GameConfig.REELS) { pickSymbol(random, weights, total) } }

        var wildDropUsed = false
        if (request.wildDropArmed) {
            val row = random.nextInt(GameConfig.ROWS)
            grid[row][2] = SlotSymbol.GOLDEN_ROOSTER
            wildDropUsed = true
        }

        val frozen = grid.map { it.toList() }
        val evaluation = evaluateLines(frozen, request.bet)
        val lineCredits = evaluation.wins.sumOf { it.credits }
        val totalWin = lineCredits + evaluation.scatterCredits

        val scatterNear = evaluation.scatterCount == 2
        val nearWin = scatterNear || evaluation.nearWinLine >= 0
        val decidingReel = when {
            scatterNear -> lastScatterReel(frozen)
            evaluation.nearWinLine >= 0 -> GameConfig.REELS - 1
            else -> -1
        }

        return SpinOutcome(
            grid = frozen,
            lineWins = evaluation.wins,
            scatterCount = evaluation.scatterCount,
            scatterCredits = evaluation.scatterCredits,
            totalWin = totalWin,
            nearWin = nearWin,
            decidingReel = decidingReel,
            bonusTriggered = evaluation.scatterCount >= 3,
            wildDropUsed = wildDropUsed,
            cardDropId = null
        )
    }

    private fun buildWeights(request: SpinRequest): Map<SlotSymbol, Float> {
        val pityActive = request.lossStreak >= GameConfig.PITY_LOSS_STREAK && request.bigWinCooldown <= 0
        return SlotSymbol.entries.associateWith { symbol ->
            val base = symbol.weight.toFloat()
            if (pityActive && symbol.isWild) base * GameConfig.PITY_WILD_FACTOR else base
        }
    }

    private fun pickSymbol(random: Random, weights: Map<SlotSymbol, Float>, total: Float): SlotSymbol {
        var roll = random.nextFloat() * total
        for ((symbol, weight) in weights) {
            roll -= weight
            if (roll <= 0f) return symbol
        }
        return SlotSymbol.FEATHER
    }

    private fun lastScatterReel(grid: List<List<SlotSymbol>>): Int {
        var reel = -1
        for (column in 0 until GameConfig.REELS) {
            for (row in 0 until GameConfig.ROWS) {
                if (grid[row][column].isScatter) reel = column
            }
        }
        return reel
    }
}
