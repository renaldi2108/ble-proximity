package id.renaldi.proximity.core.model

data class Proximity(
    val zone: ProximityZone,
    val meters: Double?,
) {
    val rangeRatio: Float
        get() {
            if (zone == ProximityZone.LOST) return 1f
            val index = REACHABLE.indexOf(zone)
            val fraction = meters?.let(::fractionWithinZone) ?: 1.0
            return ((index + fraction) / REACHABLE.size).toFloat().coerceIn(0f, 1f)
        }

    private fun fractionWithinZone(meters: Double): Double {
        val span = if (zone.isUnbounded) UNBOUNDED_SPAN_METERS else zone.farMeters - zone.nearMeters
        if (span <= 0.0) return 0.0
        return ((meters - zone.nearMeters) / span).coerceIn(0.0, 1.0)
    }

    companion object {
        val Lost = Proximity(zone = ProximityZone.LOST, meters = null)

        private val REACHABLE = ProximityZone.entries.filter(ProximityZone::isReachable)

        private const val UNBOUNDED_SPAN_METERS = 20.0

        fun of(rssi: Int, calibration: RssiCalibration = RssiCalibration.Default): Proximity {
            val zone = ProximityZone.of(rssi)
            if (zone == ProximityZone.LOST) return Lost
            return Proximity(zone = zone, meters = calibration.metersFor(rssi, zone))
        }
    }
}
