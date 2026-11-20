package com.Qarkina6.Mizkar.domain.usecase

import com.Qarkina6.Mizkar.domain.model.LineWin
import com.Qarkina6.Mizkar.domain.model.SlotSymbol

val PAY_LINES: List<List<Int>> = listOf(
    listOf(1, 1, 1, 1, 1),
    listOf(0, 0, 0, 0, 0),
    listOf(2, 2, 2, 2, 2),
    listOf(0, 1, 2, 1, 0),
    listOf(2, 1, 0, 1, 2)
)

data class LineEvaluation(
    val wins: List<LineWin>,
    val scatterCount: Int,
    val scatterCredits: Long,
    val nearWinLine: Int
)

class EvaluateLinesUseCase {

    operator fun invoke(grid: List<List<SlotSymbol>>, bet: Long): LineEvaluation {
        val wins = mutableListOf<LineWin>()
        var nearWinLine = -1

        PAY_LINES.forEachIndexed { lineIndex, pattern ->
            val symbols = pattern.mapIndexed { reel, row -> grid[row][reel] }
            if (symbols.first().isScatter) return@forEachIndexed
            val base = symbols.firstOrNull { !it.isWild && !it.isScatter } ?: SlotSymbol.GOLDEN_ROOSTER
            var count = 0
            for (symbol in symbols) {
                if (symbol == base || symbol.isWild) count++ else break
            }
            if (count >= 3) {
                val credits = (payoutOf(base, count) * bet).toLong()
                if (credits > 0L) wins.add(LineWin(lineIndex, base, count, credits))
            }
            if (count == 4 && nearWinLine < 0) nearWinLine = lineIndex
        }

        val scatterCount = grid.sumOf { row -> row.count { it.isScatter } }
        val scatterCredits = if (scatterCount >= 3) {
            (payoutOf(SlotSymbol.ROUTE_SIGN, minOf(scatterCount, 5)) * bet).toLong()
        } else {
            0L
        }

        return LineEvaluation(wins, scatterCount, scatterCredits, nearWinLine)
    }

    private fun payoutOf(symbol: SlotSymbol, count: Int): Float = when (count) {
        3 -> symbol.payThree
        4 -> symbol.payFour
        else -> symbol.payFive
    }
}
