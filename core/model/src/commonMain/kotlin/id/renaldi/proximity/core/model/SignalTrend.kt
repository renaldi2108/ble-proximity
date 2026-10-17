package id.renaldi.proximity.core.model

enum class SignalTrend {
    APPROACHING,
    STABLE,
    RECEDING,
    LOST,
    ;

    companion object {
        const val SIGNIFICANT_DELTA_DB: Int = 4

        fun of(oldestRssi: Int, latestRssi: Int, isLost: Boolean): SignalTrend = when {
            isLost -> LOST
            latestRssi - oldestRssi >= SIGNIFICANT_DELTA_DB -> APPROACHING
            oldestRssi - latestRssi >= SIGNIFICANT_DELTA_DB -> RECEDING
            else -> STABLE
        }
    }
}
