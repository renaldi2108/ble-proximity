package id.renaldi.proximity.core.model

import kotlin.math.pow

data class RssiCalibration(
    val referenceRssiAtOneMeter: Int,
    val pathLossExponent: Double,
) {
    fun rawMeters(rssi: Int): Double =
        TEN.pow((referenceRssiAtOneMeter - rssi) / (TEN * pathLossExponent))

    fun metersFor(rssi: Int, zone: ProximityZone): Double {
        val raw = rawMeters(rssi)
        if (zone.isUnbounded) return maxOf(raw, zone.nearMeters)
        return raw.coerceIn(zone.nearMeters, zone.farMeters)
    }

    companion object {
        val Default = RssiCalibration(referenceRssiAtOneMeter = -59, pathLossExponent = 2.0)

        private const val TEN = 10.0
    }
}
