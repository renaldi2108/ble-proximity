package id.renaldi.proximity.core.common.format

import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalTime::class)
object RelativeTimeFormat {
    private const val SECOND = 1_000L
    private const val MINUTE = 60 * SECOND
    private const val HOUR = 60 * MINUTE
    private const val DAY = 24 * HOUR
    private const val JUST_NOW_MILLIS = 10 * SECOND
    private const val WEEK_MILLIS = 7 * DAY

    private val MONTHS = listOf(
        "Jan", "Feb", "Mar", "Apr", "Mei", "Jun",
        "Jul", "Agu", "Sep", "Okt", "Nov", "Des",
    )

    fun format(timestampMillis: Long, nowMillis: Long): String {
        val elapsed = (nowMillis - timestampMillis).coerceAtLeast(0L)
        return when {
            elapsed < JUST_NOW_MILLIS -> "baru saja"
            elapsed < MINUTE -> "${elapsed / SECOND} detik lalu"
            elapsed < HOUR -> "${elapsed / MINUTE} menit lalu"
            elapsed < DAY -> "${elapsed / HOUR} jam lalu"
            elapsed < WEEK_MILLIS -> "${elapsed / DAY} hari lalu"
            else -> formatAbsolute(timestampMillis)
        }
    }

    fun formatAbsolute(timestampMillis: Long): String {
        val dateTime = Instant.fromEpochMilliseconds(timestampMillis)
            .toLocalDateTime(TimeZone.currentSystemDefault())
        val month = MONTHS[dateTime.month.ordinal]
        val hour = dateTime.hour.toString().padStart(2, '0')
        val minute = dateTime.minute.toString().padStart(2, '0')
        return "${dateTime.day} $month ${dateTime.year} · $hour:$minute"
    }

    fun formatDuration(durationMillis: Long): String {
        val seconds = durationMillis / SECOND
        return when {
            seconds < 60 -> "${seconds}s"
            seconds < 3_600 -> "${seconds / 60}m ${seconds % 60}s"
            else -> "${seconds / 3_600}j ${(seconds % 3_600) / 60}m"
        }
    }
}
