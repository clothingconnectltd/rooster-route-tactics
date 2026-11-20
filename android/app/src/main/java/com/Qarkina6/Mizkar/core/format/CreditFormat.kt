package com.Qarkina6.Mizkar.core.format

fun formatCredits(value: Long): String {
    val negative = value < 0
    val digits = if (negative) (-value).toString() else value.toString()
    val builder = StringBuilder()
    var counter = 0
    for (index in digits.length - 1 downTo 0) {
        builder.append(digits[index])
        counter++
        if (counter % 3 == 0 && index > 0) builder.append(',')
    }
    if (negative) builder.append('-')
    return builder.reverse().toString()
}

fun formatClock(totalSeconds: Long): String {
    val safe = if (totalSeconds < 0) 0 else totalSeconds
    val hours = safe / 3600
    val minutes = (safe % 3600) / 60
    val seconds = safe % 60
    return buildString {
        if (hours > 0) {
            append(pad(hours))
            append(':')
        }
        append(pad(minutes))
        append(':')
        append(pad(seconds))
    }
}

private fun pad(value: Long): String = if (value < 10) "0$value" else value.toString()
