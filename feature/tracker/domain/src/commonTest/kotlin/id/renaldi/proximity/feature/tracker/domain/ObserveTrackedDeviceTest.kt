package id.renaldi.proximity.feature.tracker.domain

import app.cash.turbine.test
import id.renaldi.proximity.core.model.DeviceFocus
import id.renaldi.proximity.core.model.ProximityZone
import id.renaldi.proximity.core.model.ScanTuning
import id.renaldi.proximity.core.model.SignalStability
import id.renaldi.proximity.core.model.SignalTrend
import id.renaldi.proximity.core.testing.FakeScanController
import id.renaldi.proximity.core.testing.TestDevices
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class ObserveTrackedDeviceTest {
    private val controller = FakeScanController()
    private val observe = ObserveTrackedDevice(controller, ScanTuning(traceCapacity = 4))

    @Test
    fun `tracks only the requested address`() = runTest {
        observe(TestDevices.SPEAKER).test {
            controller.emit(
                listOf(
                    TestDevices.device(TestDevices.BUDS, "Galaxy Buds Pro", rssi = -40),
                    TestDevices.device(TestDevices.SPEAKER, "JBL Flip 6", rssi = -58),
                ),
            )
            val tracked = awaitItem()

            assertEquals(TestDevices.SPEAKER, tracked.device.address)
            assertEquals(ProximityZone.GOOD, tracked.device.proximity.zone)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `trace is capped to the configured capacity`() = runTest {
        observe(TestDevices.SPEAKER).test {
            listOf(-60, -58, -56, -54, -52, -50).forEach { rssi ->
                controller.emit(listOf(TestDevices.device(rssi = rssi)))
                awaitItem()
            }
            controller.emit(listOf(TestDevices.device(rssi = -48)))
            val tracked = awaitItem()

            assertEquals(4, tracked.rssiHistory.size)
            assertEquals(listOf(-54, -52, -50, -48), tracked.rssiHistory)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `rising signal is reported as approaching`() = runTest {
        observe(TestDevices.SPEAKER).test {
            controller.emit(listOf(TestDevices.device(rssi = -70)))
            awaitItem()
            controller.emit(listOf(TestDevices.device(rssi = -55)))
            val tracked = awaitItem()

            assertEquals(SignalTrend.APPROACHING, tracked.trend)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `disappearing device keeps its last identity and turns lost`() = runTest {
        observe(TestDevices.SPEAKER).test {
            controller.emit(listOf(TestDevices.device(rssi = -55)))
            awaitItem()

            controller.emit(emptyList())
            val tracked = awaitItem()

            assertEquals(TestDevices.SPEAKER, tracked.device.address)
            assertEquals("JBL Flip 6", tracked.device.name)
            assertEquals(ProximityZone.LOST, tracked.device.proximity.zone)
            assertEquals(SignalTrend.LOST, tracked.trend)
            assertTrue(tracked.isLost)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `nothing is emitted until the device is seen at least once`() = runTest {
        observe(TestDevices.SPEAKER).test {
            controller.emit(listOf(TestDevices.device(address = TestDevices.BUDS)))
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `focus stability is adopted and then retained`() = runTest {
        val stability = SignalStability(packetsPerSecond = 8.0, jitterDb = 1.5, millisSinceLastPacket = 90)

        observe(TestDevices.SPEAKER).test {
            controller.emit(
                devices = listOf(TestDevices.device(rssi = -55)),
                focus = DeviceFocus(TestDevices.SPEAKER, stability),
            )
            assertEquals(stability, awaitItem().stability)

            controller.emit(listOf(TestDevices.device(rssi = -56)))
            assertEquals(stability, awaitItem().stability)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `focus belonging to another device is ignored`() = runTest {
        val foreign = SignalStability(packetsPerSecond = 8.0, jitterDb = 1.5, millisSinceLastPacket = 90)

        observe(TestDevices.SPEAKER).test {
            controller.emit(
                devices = listOf(TestDevices.device(rssi = -55)),
                focus = DeviceFocus(TestDevices.BUDS, foreign),
            )

            assertEquals(SignalStability.Unknown, awaitItem().stability)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
