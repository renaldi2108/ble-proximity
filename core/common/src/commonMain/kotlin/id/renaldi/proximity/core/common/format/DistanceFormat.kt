package id.renaldi.proximity.core.common.format

import id.renaldi.proximity.core.model.Proximity
import id.renaldi.proximity.core.model.ProximityZone

object DistanceFormat {
    const val UNKNOWN: String = "—"

    private const val SUB_METER_THRESHOLD = 1.0
    private const val ONE_DECIMAL_THRESHOLD = 10.0

    fun format(proximity: Proximity): String {
        val meters = proximity.meters ?: return UNKNOWN
        if (proximity.zone.isUnbounded) return "> ${proximity.zone.nearMeters.toFixed(0)} m"
        return "${meters.toMeterText()} m"
    }

    fun formatRange(zone: ProximityZone): String = when {
        zone == ProximityZone.LOST -> UNKNOWN
        zone.isUnbounded -> "> ${zone.nearMeters.toFixed(0)} m"
        zone.nearMeters == 0.0 -> "< ${zone.farMeters.toFixed(0)} m"
        else -> "${zone.nearMeters.toFixed(0)} – ${zone.farMeters.toFixed(0)} m"
    }

    private fun Double.toMeterText(): String = when {
        this < SUB_METER_THRESHOLD -> toFixed(2)
        this < ONE_DECIMAL_THRESHOLD -> toFixed(1)
        else -> toFixed(0)
    }
}
