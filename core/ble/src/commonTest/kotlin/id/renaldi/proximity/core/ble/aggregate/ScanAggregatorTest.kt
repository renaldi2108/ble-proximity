package id.renaldi.proximity.core.ble.aggregate

import id.renaldi.proximity.core.model.BleAdvertisement
import id.renaldi.proximity.core.model.BleDevice
import id.renaldi.proximity.core.model.ProximityZone
import id.renaldi.proximity.core.model.ScanTuning
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ScanAggregatorTest {
    private val tuning = ScanTuning(
        lostAfterMillis = 3_000L,
        staleAfterMillis = 30_000L,
        rssiWindow = 5,
        maxDevices = 3,
    )

    @Test
    fun `first packet becomes a visible device`() {
        val aggregator = ScanAggregator(tuning)
        aggregator.record(packet(rssi = -55, atMillis = 1_000))

        val devices = aggregator.snapshot(nowMillis = 1_000)

        assertEquals(1, devices.size)
        assertEquals(SPEAKER, devices.single().address)
        assertEquals(-55, devices.single().rawRssi)
        assertEquals(-55, devices.single().smoothedRssi)
    }

    @Test
    fun `name arriving on a later packet is retained`() {
        val aggregator = ScanAggregator(tuning)
        aggregator.record(packet(rssi = -55, atMillis = 1_000, name = null))
        aggregator.record(packet(rssi = -55, atMillis = 1_100, name = "JBL Flip 6"))
        aggregator.record(packet(rssi = -55, atMillis = 1_200, name = null))

        assertEquals("JBL Flip 6", aggregator.snapshot(1_200).single().name)
    }

    @Test
    fun `median rejects a single outlier packet`() {
        val aggregator = ScanAggregator(tuning)
        listOf(-60, -61, -20, -59, -60).forEachIndexed { index, rssi ->
            aggregator.record(packet(rssi = rssi, atMillis = 1_000L + index * 100))
        }

        val device = aggregator.snapshot(nowMillis = 1_400).single()

        assertEquals(-60, device.smoothedRssi)
        assertEquals(-60, device.rawRssi)
    }

    @Test
    fun `unchanged device reuses the same instance so compose can skip the row`() {
        val aggregator = ScanAggregator(tuning)
        aggregator.record(packet(rssi = -55, atMillis = 1_000))

        val first = aggregator.snapshot(nowMillis = 1_000).single()
        val second = aggregator.snapshot(nowMillis = 1_100).single()

        assertSame(first, second)
    }

    @Test
    fun `new packet produces a fresh instance`() {
        val aggregator = ScanAggregator(tuning)
        aggregator.record(packet(rssi = -55, atMillis = 1_000))
        val first = aggregator.snapshot(nowMillis = 1_000).single()

        aggregator.record(packet(rssi = -70, atMillis = 1_100))
        val second = aggregator.snapshot(nowMillis = 1_100).single()

        assertNotSame(first, second)
        assertEquals(-70, second.rawRssi)
    }

    @Test
    fun `silence past the lost window reports the device as lost`() {
        val aggregator = ScanAggregator(tuning)
        aggregator.record(packet(rssi = -55, atMillis = 1_000))

        val stillVisible = aggregator.snapshot(nowMillis = 3_500).single()
        val lost = aggregator.snapshot(nowMillis = 5_000).single()

        assertEquals(ProximityZone.GOOD, stillVisible.proximity.zone)
        assertEquals(ProximityZone.LOST, lost.proximity.zone)
        assertEquals(BleDevice.LOST_RSSI, lost.smoothedRssi)
        assertEquals(-55, lost.rawRssi)
    }

    @Test
    fun `stale device is dropped once its packets are persisted`() {
        val aggregator = ScanAggregator(tuning)
        aggregator.record(packet(rssi = -55, atMillis = 1_000))
        aggregator.drainSightings()

        assertTrue(aggregator.snapshot(nowMillis = 40_000).isEmpty())
        assertEquals(0, aggregator.trackedCount)
    }

    @Test
    fun `stale device survives until its packets reach the database`() {
        val aggregator = ScanAggregator(tuning)
        aggregator.record(packet(rssi = -55, atMillis = 1_000))

        assertEquals(1, aggregator.snapshot(nowMillis = 40_000).size)
    }

    @Test
    fun `tracked devices stay bounded by evicting the oldest sighting`() {
        val aggregator = ScanAggregator(tuning)
        repeat(6) { index ->
            aggregator.record(
                packet(address = "AA:BB:CC:DD:EE:0$index", rssi = -60, atMillis = 1_000L + index),
            )
        }

        assertEquals(tuning.maxDevices, aggregator.trackedCount)
        val addresses = aggregator.snapshot(nowMillis = 1_010).map { it.address }
        assertEquals(listOf("AA:BB:CC:DD:EE:03", "AA:BB:CC:DD:EE:04", "AA:BB:CC:DD:EE:05"), addresses.sorted())
    }

    @Test
    fun `sightings drain once and report the packet delta`() {
        val aggregator = ScanAggregator(tuning)
        aggregator.record(packet(rssi = -50, atMillis = 1_000))
        aggregator.record(packet(rssi = -70, atMillis = 1_100))

        val first = aggregator.drainSightings().single()

        assertEquals(2L, first.packetDelta)
        assertEquals(-50, first.strongestRssi)
        assertEquals(-70, first.lastRssi)
        assertEquals(1_000L, first.firstSeenAtMillis)
        assertEquals(1_100L, first.lastSeenAtMillis)
        assertTrue(aggregator.drainSightings().isEmpty())
    }

    @Test
    fun `stability reports packet rate and jitter`() {
        val aggregator = ScanAggregator(tuning)
        listOf(-60, -60, -60, -60, -60).forEachIndexed { index, rssi ->
            aggregator.record(packet(rssi = rssi, atMillis = 1_000L + index * 100))
        }

        val stability = aggregator.stabilityOf(SPEAKER, nowMillis = 1_500)

        checkNotNull(stability)
        assertEquals(10.0, stability.packetsPerSecond)
        assertEquals(0.0, stability.jitterDb)
        assertEquals(100L, stability.millisSinceLastPacket)
    }

    @Test
    fun `jitter grows with an unstable signal`() {
        val aggregator = ScanAggregator(tuning)
        listOf(-50, -70, -50, -70, -50).forEachIndexed { index, rssi ->
            aggregator.record(packet(rssi = rssi, atMillis = 1_000L + index * 100))
        }

        val stability = checkNotNull(aggregator.stabilityOf(SPEAKER, nowMillis = 1_400))

        assertTrue(stability.jitterDb > 6.0, "expected jitter above 6 dB, got ${stability.jitterDb}")
    }

    @Test
    fun `stability of an unknown address is absent`() {
        assertNull(ScanAggregator(tuning).stabilityOf("00:00:00:00:00:00", nowMillis = 0))
    }

    @Test
    fun `reset clears every tracked device`() {
        val aggregator = ScanAggregator(tuning)
        aggregator.record(packet(rssi = -55, atMillis = 1_000))
        aggregator.reset()

        assertEquals(0, aggregator.trackedCount)
        assertTrue(aggregator.snapshot(nowMillis = 1_000).isEmpty())
    }

    private fun packet(
        address: String = SPEAKER,
        rssi: Int,
        atMillis: Long,
        name: String? = "JBL Flip 6",
    ) = BleAdvertisement(
        address = address,
        name = name,
        rssi = rssi,
        txPower = null,
        isConnectable = true,
        timestampMillis = atMillis,
    )

    private companion object {
        const val SPEAKER = "A0:6F:32:1C:48:9D"
    }
}
