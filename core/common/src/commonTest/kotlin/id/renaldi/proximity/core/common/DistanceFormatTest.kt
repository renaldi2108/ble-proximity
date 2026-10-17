package id.renaldi.proximity.core.common

import id.renaldi.proximity.core.common.format.DistanceFormat
import id.renaldi.proximity.core.model.Proximity
import id.renaldi.proximity.core.model.ProximityZone
import kotlin.test.Test
import kotlin.test.assertEquals

class DistanceFormatTest {
    @Test
    fun `sub meter distance keeps two decimals`() {
        val formatted = DistanceFormat.format(Proximity(ProximityZone.IMMEDIATE, 0.4267))
        assertEquals("0,43 m", formatted)
    }

    @Test
    fun `mid range distance keeps one decimal`() {
        val formatted = DistanceFormat.format(Proximity(ProximityZone.GOOD, 3.4567))
        assertEquals("3,5 m", formatted)
    }

    @Test
    fun `long distance is rounded to whole meters`() {
        val formatted = DistanceFormat.format(Proximity(ProximityZone.WEAK, 14.6))
        assertEquals("15 m", formatted)
    }

    @Test
    fun `unbounded zone is reported as a floor`() {
        val formatted = DistanceFormat.format(Proximity(ProximityZone.VERY_WEAK, 42.0))
        assertEquals("> 20 m", formatted)
    }

    @Test
    fun `lost signal has no distance text`() {
        assertEquals(DistanceFormat.UNKNOWN, DistanceFormat.format(Proximity.Lost))
    }

    @Test
    fun `range text mirrors the study case table`() {
        assertEquals("< 1 m", DistanceFormat.formatRange(ProximityZone.IMMEDIATE))
        assertEquals("1 – 3 m", DistanceFormat.formatRange(ProximityZone.NEAR))
        assertEquals("3 – 10 m", DistanceFormat.formatRange(ProximityZone.GOOD))
        assertEquals("10 – 20 m", DistanceFormat.formatRange(ProximityZone.WEAK))
        assertEquals("> 20 m", DistanceFormat.formatRange(ProximityZone.VERY_WEAK))
        assertEquals(DistanceFormat.UNKNOWN, DistanceFormat.formatRange(ProximityZone.LOST))
    }
}
