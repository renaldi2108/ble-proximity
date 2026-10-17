package id.renaldi.proximity.core.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ProximityTest {
    @Test
    fun `estimate stays inside the bracket advertised to the user`() {
        ProximityZone.entries.filter { it.isReachable && !it.isUnbounded }.forEach { zone ->
            for (rssi in zone.minRssi..bracketCeiling(zone)) {
                val meters = Proximity.of(rssi).meters ?: error("no estimate for $rssi dBm")
                assertTrue(
                    meters in zone.nearMeters..zone.farMeters,
                    "$rssi dBm estimated ${meters}m, outside $zone (${zone.nearMeters}..${zone.farMeters})",
                )
            }
        }
    }

    @Test
    fun `unbounded zone is only floored`() {
        val proximity = Proximity.of(-88)
        assertEquals(ProximityZone.VERY_WEAK, proximity.zone)
        assertTrue((proximity.meters ?: 0.0) >= ProximityZone.VERY_WEAK.nearMeters)
    }

    @Test
    fun `lost signal has no distance`() {
        val proximity = Proximity.of(-95)
        assertEquals(ProximityZone.LOST, proximity.zone)
        assertNull(proximity.meters)
    }

    @Test
    fun `distance grows monotonically as signal weakens`() {
        val stronger = Proximity.of(-45).meters ?: error("missing")
        val weaker = Proximity.of(-65).meters ?: error("missing")
        assertTrue(weaker > stronger, "expected $weaker > $stronger")
    }

    @Test
    fun `reference rssi resolves to one meter`() {
        val calibration = RssiCalibration.Default
        val meters = calibration.rawMeters(calibration.referenceRssiAtOneMeter)
        assertTrue(meters in 0.99..1.01, "expected ~1.0m, got $meters")
    }

    private fun bracketCeiling(zone: ProximityZone): Int =
        ProximityZone.entries
            .filter { it.minRssi > zone.minRssi }
            .minOfOrNull { it.minRssi - 1 }
            ?: 0
}
