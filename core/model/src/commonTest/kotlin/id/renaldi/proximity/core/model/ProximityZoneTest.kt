package id.renaldi.proximity.core.model

import kotlin.test.Test
import kotlin.test.assertEquals

class ProximityZoneTest {
    @Test
    fun `maps every bracket of the study case table`() {
        assertEquals(ProximityZone.IMMEDIATE, ProximityZone.of(-10))
        assertEquals(ProximityZone.IMMEDIATE, ProximityZone.of(-29))
        assertEquals(ProximityZone.NEAR, ProximityZone.of(-31))
        assertEquals(ProximityZone.NEAR, ProximityZone.of(-49))
        assertEquals(ProximityZone.GOOD, ProximityZone.of(-51))
        assertEquals(ProximityZone.GOOD, ProximityZone.of(-69))
        assertEquals(ProximityZone.WEAK, ProximityZone.of(-71))
        assertEquals(ProximityZone.WEAK, ProximityZone.of(-79))
        assertEquals(ProximityZone.VERY_WEAK, ProximityZone.of(-81))
        assertEquals(ProximityZone.VERY_WEAK, ProximityZone.of(-89))
        assertEquals(ProximityZone.LOST, ProximityZone.of(-91))
    }

    @Test
    fun `resolves shared boundaries to the stronger zone`() {
        assertEquals(ProximityZone.IMMEDIATE, ProximityZone.of(-30))
        assertEquals(ProximityZone.NEAR, ProximityZone.of(-50))
        assertEquals(ProximityZone.GOOD, ProximityZone.of(-70))
        assertEquals(ProximityZone.WEAK, ProximityZone.of(-80))
        assertEquals(ProximityZone.VERY_WEAK, ProximityZone.of(-90))
    }

    @Test
    fun `never returns null for implausible readings`() {
        assertEquals(ProximityZone.IMMEDIATE, ProximityZone.of(0))
        assertEquals(ProximityZone.LOST, ProximityZone.of(-200))
    }

    @Test
    fun `only lost zone is unreachable`() {
        val unreachable = ProximityZone.entries.filterNot { it.isReachable }
        assertEquals(listOf(ProximityZone.LOST), unreachable)
    }
}
