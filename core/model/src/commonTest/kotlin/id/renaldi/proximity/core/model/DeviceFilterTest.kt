package id.renaldi.proximity.core.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DeviceFilterTest {
    private val speaker = device(address = "AA:BB:CC:DD:EE:01", name = "JBL Flip 6", rssi = -55)
    private val anonymous = device(address = "11:22:33:44:55:66", name = null, rssi = -85)

    @Test
    fun `default filter admits everything`() {
        assertTrue(DeviceFilter.Default.matches(speaker))
        assertTrue(DeviceFilter.Default.matches(anonymous))
        assertFalse(DeviceFilter.Default.isActive)
    }

    @Test
    fun `query matches name case insensitively`() {
        assertTrue(DeviceFilter(query = "jbl").matches(speaker))
        assertTrue(DeviceFilter(query = "Flip").matches(speaker))
        assertFalse(DeviceFilter(query = "jbl").matches(anonymous))
    }

    @Test
    fun `query matches partial mac address`() {
        assertTrue(DeviceFilter(query = "cc:dd").matches(speaker))
        assertTrue(DeviceFilter(query = "EE:01").matches(speaker))
        assertFalse(DeviceFilter(query = "ff:ff").matches(speaker))
    }

    @Test
    fun `query is trimmed before matching`() {
        assertTrue(DeviceFilter(query = "  jbl  ").matches(speaker))
    }

    @Test
    fun `threshold rejects weaker devices`() {
        val filter = DeviceFilter(minRssi = -80)
        assertTrue(filter.matches(speaker))
        assertFalse(filter.matches(anonymous))
    }

    @Test
    fun `threshold boundary is inclusive`() {
        assertTrue(DeviceFilter(minRssi = -55).matches(speaker))
        assertFalse(DeviceFilter(minRssi = -54).matches(speaker))
    }

    @Test
    fun `only named hides anonymous devices`() {
        val filter = DeviceFilter(onlyNamed = true)
        assertTrue(filter.matches(speaker))
        assertFalse(filter.matches(anonymous))
    }

    @Test
    fun `active count reports how many constraints are set`() {
        assertEquals(0, DeviceFilter.Default.activeCount)
        assertEquals(2, DeviceFilter(query = "jbl", minRssi = -70).activeCount)
        assertEquals(
            4,
            DeviceFilter(
                query = "jbl",
                minRssi = -70,
                onlyNamed = true,
                sort = DeviceSort.NAME,
            ).activeCount,
        )
    }

    private fun device(address: String, name: String?, rssi: Int) = BleDevice(
        address = address,
        name = name,
        rawRssi = rssi,
        smoothedRssi = rssi,
        lastSeenAtMillis = 0L,
        firstSeenAtMillis = 0L,
        packetCount = 1,
        isConnectable = true,
    )
}
