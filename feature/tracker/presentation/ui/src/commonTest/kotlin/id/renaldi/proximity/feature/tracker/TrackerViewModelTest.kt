package id.renaldi.proximity.feature.tracker

import app.cash.turbine.test
import id.renaldi.proximity.core.domain.usecase.ObserveBleReadiness
import id.renaldi.proximity.feature.tracker.domain.ObserveTrackedDevice
import id.renaldi.proximity.core.model.BleFailure
import id.renaldi.proximity.core.model.BleReadiness
import id.renaldi.proximity.core.model.DeviceFocus
import id.renaldi.proximity.core.model.ProximityZone
import id.renaldi.proximity.core.model.ScanStatus
import id.renaldi.proximity.core.model.SignalStability
import id.renaldi.proximity.core.model.SignalTrend
import id.renaldi.proximity.core.testing.FakeBleEnvironment
import id.renaldi.proximity.core.testing.FakeScanController
import id.renaldi.proximity.core.testing.TestDevices
import id.renaldi.proximity.core.testing.awaitUntil
import id.renaldi.proximity.core.testing.runViewModelTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TrackerViewModelTest {
    private val controller = FakeScanController()
    private val environment = FakeBleEnvironment()

    private fun viewModel(
        address: String = TestDevices.SPEAKER,
        environment: FakeBleEnvironment = this.environment,
    ) = TrackerViewModel(
        address = address,
        controller = controller,
        observeTrackedDevice = ObserveTrackedDevice(controller),
        observeReadiness = ObserveBleReadiness(environment),
    )

    @Test
    fun `focuses the tracked address so stability is computed for it`() = runViewModelTest {
        viewModel()

        assertEquals(TestDevices.SPEAKER, controller.focusedAddress)
    }

    @Test
    fun `waits for the first sighting before showing a radar`() = runViewModelTest {
        viewModel().state.test {
            val state = awaitItem()
            assertTrue(state.isAcquiring)
            assertEquals(ProximityZone.LOST, state.zone)
            assertEquals(TestDevices.SPEAKER, state.title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `adopts the device identity once it is seen`() = runViewModelTest {
        val model = viewModel()

        model.state.test {
            awaitItem()
            controller.emit(listOf(TestDevices.device(rssi = -45)))

            val state = awaitUntil { it.tracked != null }
            assertEquals("JBL Flip 6", state.title)
            assertEquals(ProximityZone.NEAR, state.zone)
            assertFalse(state.isAcquiring)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ignores devices other than the target`() = runViewModelTest {
        val model = viewModel()

        model.state.test {
            awaitItem()
            controller.emit(listOf(TestDevices.device(address = TestDevices.BUDS)))
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `a device that drops out is reported as lost rather than disappearing`() =
        runViewModelTest {
            val model = viewModel()

            model.state.test {
                awaitItem()
                controller.emit(listOf(TestDevices.device(rssi = -45)))
                awaitUntil { it.tracked != null }

                controller.emit(emptyList())
                val state = awaitUntil { it.zone == ProximityZone.LOST }

                assertEquals("JBL Flip 6", state.title)
                assertEquals(SignalTrend.LOST, state.tracked?.trend)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `stability from the focused snapshot reaches the state`() = runViewModelTest {
        val stability = SignalStability(
            packetsPerSecond = 9.0,
            jitterDb = 1.2,
            millisSinceLastPacket = 80,
        )
        val model = viewModel()

        model.state.test {
            awaitItem()
            controller.emit(
                devices = listOf(TestDevices.device(rssi = -45)),
                focus = DeviceFocus(TestDevices.SPEAKER, stability),
            )

            assertEquals(stability, awaitUntil { it.tracked != null }.tracked?.stability)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggle controls the shared scan`() = runViewModelTest {
        val model = viewModel()

        model.state.test {
            awaitItem()
            model.onEvent(TrackerAction.ToggleScan)
            assertTrue(awaitUntil { it.isScanning }.isScanning)
            assertEquals(1, controller.startCount)

            model.onEvent(TrackerAction.ToggleScan)
            assertFalse(awaitUntil { !it.isScanning }.isScanning)
            assertEquals(1, controller.stopCount)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `blocked bluetooth surfaces on the tracking screen too`() = runViewModelTest {
        val blocked = FakeBleEnvironment(BleReadiness.Blocked(BleFailure.BluetoothOff))

        viewModel(environment = blocked).state.test {
            assertEquals(BleFailure.BluetoothOff, awaitUntil { it.blocker != null }.blocker)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `scan rejection is shown over readiness`() = runViewModelTest {
        val model = viewModel()

        model.state.test {
            awaitItem()
            controller.setStatus(ScanStatus.Failed(BleFailure.ScanRejected(code = 1)))

            assertEquals(
                BleFailure.ScanRejected(1),
                awaitUntil { it.blocker != null }.blocker,
            )
            cancelAndIgnoreRemainingEvents()
        }
    }
}
