package id.renaldi.proximity.core.testing

import id.renaldi.proximity.core.common.AppClock

class MutableAppClock(private var current: Long = 0L) : AppClock {
    override fun nowMillis(): Long = current

    fun set(millis: Long) {
        current = millis
    }

    fun advance(millis: Long) {
        current += millis
    }
}
