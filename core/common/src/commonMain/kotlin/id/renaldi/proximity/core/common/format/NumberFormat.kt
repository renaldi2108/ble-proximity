package id.renaldi.proximity.core.common.format

import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.roundToLong

private const val DECIMAL_SEPARATOR = ','

fun Double.toFixed(decimals: Int): String {
    if (isNaN() || isInfinite()) return "-"
    if (decimals <= 0) return roundToLong().toString()
    var scale = 1L
    repeat(decimals) { scale *= 10 }
    val scaled = (abs(this) * scale).roundToLong()
    val whole = scaled / scale
    val fraction = (scaled % scale).toString().padStart(decimals, '0')
    val sign = if (this < 0) "-" else ""
    return "$sign$whole$DECIMAL_SEPARATOR$fraction"
}

fun Double.toPercent(): Int = (this * 100).roundToInt()
