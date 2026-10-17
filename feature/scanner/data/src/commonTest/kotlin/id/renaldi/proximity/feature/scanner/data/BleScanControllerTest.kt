package id.renaldi.proximity.feature.scanner.data

import id.renaldi.proximity.core.domain.port.BleEnvironment
import id.renaldi.proximity.core.domain.port.ThermalMonitor
import id.renaldi.proximity.core.model.BleFailure
import id.renaldi.proximity.core.model.BleReadiness
import id.renaldi.proximity.core.model.ProximityZone
import id.renaldi.proximity.core.model.ScanPower
import id.renaldi.proximity.core.model.ScanStatus
import id.renaldi.proximity.core.testing.FakeBleEnvironment
import id.renaldi.proximity.core.testing.FakeBleScanner
import id.renaldi.proximity.core.testing.FakeDeviceHistoryRepository
import id.renaldi.proximity.core.testing.MutableAppClock
import id.renaldi.proximity.core.testing.TestDispatcherProvider
import id.renaldi.proximity.core.testing.TestDevices
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class BleScanControllerTest {
    @Test
    fun `idle until start is requested`() = runTest {
        val fixture = fixture()

        assertEquals(ScanStatus.Idle, fixture.controller.status.value)
        assertEquals(0, fixture.scanner.startCount)
    }

    @Test
    fun `start begins scanning and publishes throttled snapshots`() = runTest {
        val fixture = fixture()
        fixture.controller.start()
        runCurrent()

        assertEquals(ScanStatus.Scanning, fixture.controller.status.value)
        assertEquals(1, fixture.scanner.startCount)

        fixture.scanner.emit(TestDevices.SPEAKER, rssi = -55, atMillis = 0, name = "JBL Flip 6")
        fixture.scanner.emit(TestDevices.BUDS, rssi = -40, atMillis = 0, name = "Galaxy Buds Pro")
        advanceTimeBy(300)
        runCurrent()

        val snapshot = fixture.controller.snapshots.first()
        assertEquals(2, snapshot.devices.size)
        assertEquals(TestDevices.BUDS, snapshot.devices.maxBy { it.smoothedRssi }.address)
    }

    @Test
    fun `many packets collapse into one snapshot per interval`() = runTest {
        val fixture = fixture()
        fixture.controller.start()
        runCurrent()

        var emissions = 0
        val job = collectSnapshots(fixture) { emissions++ }

        repeat(200) { index ->
            fixture.scanner.emit(TestDevices.SPEAKER, rssi = -55 - index % 5, atMillis = index.toLong())
        }
        advanceTimeBy(1_100)
        runCurrent()
        job.cancel()

        assertTrue(emissions in 2..6, "200 packets should collapse to a handful of snapshots, got $emissions")
    }

    @Test
    fun `stop cancels the scan and clears the live list`() = runTest {
        val fixture = fixture()
        fixture.controller.start()
        runCurrent()
        fixture.scanner.emit(TestDevices.SPEAKER, rssi = -55, atMillis = 0)
        advanceTimeBy(300)
        runCurrent()

        fixture.controller.stop()
        runCurrent()

        assertEquals(ScanStatus.Idle, fixture.controller.status.value)
        assertTrue(fixture.controller.snapshots.first().devices.isEmpty())
    }

    @Test
    fun `start is idempotent while already scanning`() = runTest {
        val fixture = fixture()
        fixture.controller.start()
        runCurrent()
        fixture.controller.start()
        runCurrent()

        assertEquals(1, fixture.scanner.startCount)
    }

    @Test
    fun `blocked environment fails fast without touching the radio`() = runTest {
        val fixture = fixture(environment = FakeBleEnvironment(BleReadiness.Blocked(BleFailure.BluetoothOff)))
        fixture.controller.start()
        runCurrent()

        assertEquals(ScanStatus.Failed(BleFailure.BluetoothOff), fixture.controller.status.value)
        assertEquals(0, fixture.scanner.startCount)
    }

    @Test
    fun `bluetooth switched off mid scan surfaces as a failure`() = runTest {
        val fixture = fixture()
        fixture.controller.start()
        runCurrent()

        (fixture.environment as FakeBleEnvironment).set(BleReadiness.Blocked(BleFailure.BluetoothOff))
        runCurrent()

        assertEquals(ScanStatus.Failed(BleFailure.BluetoothOff), fixture.controller.status.value)
    }

    @Test
    fun `an environment that publishes no changes does not abort the scan`() = runTest {
        val silent = object : BleEnvironment {
            override fun readiness() = BleReadiness.Ready
            override fun readinessChanges(): Flow<BleReadiness> = emptyFlow()
            override fun refresh() = Unit
        }
        val fixture = fixture(environment = silent)
        fixture.controller.start()
        runCurrent()

        fixture.scanner.emit(TestDevices.SPEAKER, rssi = -55, atMillis = 0)
        advanceTimeBy(300)
        runCurrent()

        assertEquals(ScanStatus.Scanning, fixture.controller.status.value)
        assertEquals(1, fixture.controller.snapshots.first().devices.size)
    }

    @Test
    fun `scanner rejection surfaces the platform error code`() = runTest {
        val fixture = fixture(scanner = FakeBleScanner(failWith = BleFailure.ScanRejected(code = 2)))
        fixture.controller.start()
        runCurrent()

        assertEquals(ScanStatus.Failed(BleFailure.ScanRejected(2)), fixture.controller.status.value)
    }

    @Test
    fun `backgrounding pauses the radio and foregrounding resumes it`() = runTest {
        val fixture = fixture()
        fixture.controller.start()
        runCurrent()

        fixture.controller.onAppBackgrounded()
        runCurrent()
        fixture.controller.onAppForegrounded()
        runCurrent()

        assertEquals(2, fixture.scanner.startCount)
    }

    @Test
    fun `lifecycle callbacks do nothing when the user never started a scan`() = runTest {
        val fixture = fixture()

        fixture.controller.onAppForegrounded()
        fixture.controller.onAppBackgrounded()
        runCurrent()

        assertEquals(0, fixture.scanner.startCount)
    }

    @Test
    fun `sightings reach the database in batches rather than per packet`() = runTest {
        val fixture = fixture()
        fixture.controller.start()
        runCurrent()

        repeat(50) { index ->
            fixture.scanner.emit(TestDevices.SPEAKER, rssi = -55, atMillis = index.toLong())
        }
        advanceTimeBy(5_200)
        runCurrent()

        assertEquals(1, fixture.history.batches.size)
        assertEquals(50L, fixture.history.batches.single().single().packetDelta)
    }

    @Test
    fun `pending sightings are flushed when the scan stops`() = runTest {
        val fixture = fixture()
        fixture.controller.start()
        runCurrent()
        fixture.scanner.emit(TestDevices.SPEAKER, rssi = -55, atMillis = 0)
        advanceTimeBy(300)
        runCurrent()

        fixture.controller.stop()
        advanceTimeBy(100)
        runCurrent()

        assertEquals(1, fixture.history.batches.size)
    }

    @Test
    fun `focused device carries stability data`() = runTest {
        val fixture = fixture()
        fixture.controller.focus(TestDevices.SPEAKER)
        fixture.controller.start()
        runCurrent()

        repeat(5) { index ->
            fixture.scanner.emit(TestDevices.SPEAKER, rssi = -55, atMillis = index * 100L)
        }
        advanceTimeBy(300)
        runCurrent()

        val focus = fixture.controller.snapshots.first().focus
        assertEquals(TestDevices.SPEAKER, focus?.address)
        assertEquals(10.0, focus?.stability?.packetsPerSecond)
    }

    @Test
    fun `silent device decays to the lost zone`() = runTest {
        val fixture = fixture()
        fixture.controller.start()
        runCurrent()
        fixture.scanner.emit(TestDevices.SPEAKER, rssi = -55, atMillis = 0)
        advanceTimeBy(300)
        runCurrent()
        fixture.clock.set(10_000)
        advanceTimeBy(1_000)
        runCurrent()

        val device = fixture.controller.snapshots.first().devices.single()
        assertEquals(ProximityZone.LOST, device.proximity.zone)
    }

    @Test
    fun `thermal throttling downgrades the scan power`() = runTest {
        val throttling = MutableStateFlow(false)
        val fixture = fixture(thermal = ThermalMonitor { throttling })
        fixture.controller.start()
        runCurrent()
        assertEquals(ScanPower.BALANCED, fixture.scanner.lastTuning?.power)

        throttling.value = true
        runCurrent()

        assertEquals(ScanPower.LOW_POWER, fixture.scanner.lastTuning?.power)
        assertEquals(2, fixture.scanner.startCount)
    }

    private fun TestScope.collectSnapshots(fixture: Fixture, onSnapshot: () -> Unit): Job =
        launch(StandardTestDispatcher(testScheduler)) {
            fixture.controller.snapshots.collect { onSnapshot() }
        }

    private class Fixture(
        val controller: BleScanController,
        val scanner: FakeBleScanner,
        val environment: BleEnvironment,
        val history: FakeDeviceHistoryRepository,
        val clock: MutableAppClock,
    )

    private fun TestScope.fixture(
        scanner: FakeBleScanner = FakeBleScanner(),
        environment: BleEnvironment = FakeBleEnvironment(),
        thermal: ThermalMonitor = ThermalMonitor { MutableStateFlow(false) },
    ): Fixture {
        val history = FakeDeviceHistoryRepository()
        val clock = MutableAppClock()
        val controller = BleScanController(
            scanner = scanner,
            environment = environment,
            history = history,
            thermal = thermal,
            clock = clock,
            scope = backgroundScope,
            dispatchers = TestDispatcherProvider(StandardTestDispatcher(testScheduler)),
        )
        return Fixture(controller, scanner, environment, history, clock)
    }
}
