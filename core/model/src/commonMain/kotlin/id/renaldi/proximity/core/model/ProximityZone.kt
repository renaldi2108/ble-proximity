package id.renaldi.proximity.core.model

private const val UNBOUNDED = Double.POSITIVE_INFINITY

enum class ProximityZone(
    val minRssi: Int,
    val nearMeters: Double,
    val farMeters: Double,
) {
    IMMEDIATE(minRssi = -30, nearMeters = 0.0, farMeters = 1.0),
    NEAR(minRssi = -50, nearMeters = 1.0, farMeters = 3.0),
    GOOD(minRssi = -70, nearMeters = 3.0, farMeters = 10.0),
    WEAK(minRssi = -80, nearMeters = 10.0, farMeters = 20.0),
    VERY_WEAK(minRssi = -90, nearMeters = 20.0, farMeters = UNBOUNDED),
    LOST(minRssi = Int.MIN_VALUE, nearMeters = UNBOUNDED, farMeters = UNBOUNDED),
    ;

    val isUnbounded: Boolean get() = farMeters == UNBOUNDED_METERS

    val isReachable: Boolean get() = this != LOST

    companion object {
        const val UNBOUNDED_METERS: Double = UNBOUNDED

        private val ORDERED = entries.sortedByDescending { it.minRssi }

        fun of(rssi: Int): ProximityZone = ORDERED.first { rssi >= it.minRssi }
    }
}
