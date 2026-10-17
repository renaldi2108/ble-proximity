package id.renaldi.proximity.core.common

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

fun interface AppClock {
    fun nowMillis(): Long
}

@OptIn(ExperimentalTime::class)
object SystemAppClock : AppClock {
    override fun nowMillis(): Long = Clock.System.now().toEpochMilliseconds()
}
