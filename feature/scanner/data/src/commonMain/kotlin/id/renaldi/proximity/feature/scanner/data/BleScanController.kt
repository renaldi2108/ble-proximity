package id.renaldi.proximity.feature.scanner.data

import id.renaldi.proximity.core.ble.aggregate.ScanAggregator
import id.renaldi.proximity.core.common.AppClock
import id.renaldi.proximity.core.common.DispatcherProvider
import id.renaldi.proximity.core.domain.port.BleEnvironment
import id.renaldi.proximity.core.domain.port.BleScanException
import id.renaldi.proximity.core.domain.port.BleScanner
import id.renaldi.proximity.core.domain.port.DeviceHistoryRepository
import id.renaldi.proximity.core.domain.port.ScanController
import id.renaldi.proximity.core.domain.port.ThermalMonitor
import id.renaldi.proximity.core.model.BleFailure
import id.renaldi.proximity.core.model.BleReadiness
import id.renaldi.proximity.core.model.DeviceFocus
import id.renaldi.proximity.core.model.ScanSnapshot
import id.renaldi.proximity.core.model.ScanStatus
import id.renaldi.proximity.core.model.ScanTuning
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BleScanController(
    private val scanner: BleScanner,
    private val environment: BleEnvironment,
    private val history: DeviceHistoryRepository,
    private val thermal: ThermalMonitor,
    private val clock: AppClock,
    private val scope: CoroutineScope,
    dispatchers: DispatcherProvider,
) : ScanController {
    private val aggregationDispatcher = dispatchers.confined

    private val statusState = MutableStateFlow<ScanStatus>(ScanStatus.Idle)
    override val status: StateFlow<ScanStatus> = statusState.asStateFlow()

    private val snapshotState = MutableStateFlow(ScanSnapshot.Empty)
    override val snapshots: Flow<ScanSnapshot> = snapshotState.asStateFlow()

    private val focusedAddress = MutableStateFlow<String?>(null)
    private val scanRequested = MutableStateFlow(false)
    private val tuning = MutableStateFlow(ScanTuning.Default)

    private var scanJob: Job? = null

    init {
        scope.launch {
            thermal.isThrottling().distinctUntilChanged().collect(::onThermalChanged)
        }
    }

    override fun start() {
        scanRequested.value = true
        launchScan()
    }

    override fun stop() {
        scanRequested.value = false
        cancelScan()
        statusState.value = ScanStatus.Idle
        snapshotState.value = ScanSnapshot.Empty
    }

    override fun focus(address: String?) {
        focusedAddress.value = address
    }

    override fun onAppForegrounded() {
        if (scanRequested.value) launchScan()
    }

    override fun onAppBackgrounded() {
        if (scanRequested.value) cancelScan()
    }

    private fun onThermalChanged(throttling: Boolean) {
        val next = if (throttling) ScanTuning.Thermal else ScanTuning.Default
        if (tuning.value == next) return
        tuning.value = next
        if (scanJob?.isActive != true) return
        cancelScan()
        launchScan()
    }

    private fun launchScan() {
        if (scanJob?.isActive == true) return
        val readiness = environment.readiness()
        if (readiness is BleReadiness.Blocked) {
            onScanFailed(readiness.failure)
            return
        }
        statusState.value = ScanStatus.Scanning
        scanJob = scope.launch(aggregationDispatcher) { runScan(tuning.value) }
    }

    private fun cancelScan() {
        scanJob?.cancel()
        scanJob = null
    }

    private suspend fun runScan(active: ScanTuning) {
        val aggregator = ScanAggregator(active)
        try {
            coroutineScope {
                launch { scanner.advertisements(active).collect(aggregator::record) }
                launch { emitSnapshots(aggregator, active) }
                launch { persistSightings(aggregator, active) }
                launch { failWhenEnvironmentBlocks() }
            }
        } catch (failure: BleScanException) {
            onScanFailed(failure.failure)
        } finally {
            withContext(NonCancellable) { history.record(aggregator.drainSightings()) }
        }
    }

    private suspend fun emitSnapshots(aggregator: ScanAggregator, active: ScanTuning) {
        while (currentCoroutineContext().isActive) {
            delay(active.emitIntervalMillis)
            val now = clock.nowMillis()
            val devices = aggregator.snapshot(now)
            val focus = focusedAddress.value?.let { address ->
                aggregator.stabilityOf(address, now)?.let { DeviceFocus(address, it) }
            }
            val previous = snapshotState.value
            val changed = previous.devices != devices || previous.focus != focus
            val heartbeat = now - previous.capturedAtMillis >= HEARTBEAT_MILLIS
            if (changed || heartbeat) {
                snapshotState.value = ScanSnapshot(devices, now, focus)
            }
        }
    }

    private suspend fun persistSightings(aggregator: ScanAggregator, active: ScanTuning) {
        while (currentCoroutineContext().isActive) {
            delay(active.persistIntervalMillis)
            history.record(aggregator.drainSightings())
        }
    }

    private suspend fun failWhenEnvironmentBlocks(): Nothing {
        val blocked = environment.readinessChanges()
            .filterIsInstance<BleReadiness.Blocked>()
            .firstOrNull()
            ?: awaitCancellation()
        throw BleScanException(blocked.failure)
    }

    private fun onScanFailed(failure: BleFailure) {
        statusState.value = ScanStatus.Failed(failure)
        snapshotState.value = ScanSnapshot.Empty
    }

    private companion object {
        const val HEARTBEAT_MILLIS = 1_000L
    }
}
