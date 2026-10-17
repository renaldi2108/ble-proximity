package id.renaldi.proximity.feature.tracker.domain

import id.renaldi.proximity.core.domain.port.ScanController
import id.renaldi.proximity.core.model.ProximityZone
import id.renaldi.proximity.core.model.ScanSnapshot
import id.renaldi.proximity.core.model.ScanTuning
import id.renaldi.proximity.core.model.SignalStability
import id.renaldi.proximity.core.model.SignalTrend
import id.renaldi.proximity.core.model.TrackedDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.scan

class ObserveTrackedDevice(
    private val controller: ScanController,
    private val tuning: ScanTuning = ScanTuning.Default,
) {
    operator fun invoke(address: String): Flow<TrackedDevice> = controller.snapshots
        .scan<ScanSnapshot, TrackedDevice?>(null) { previous, snapshot ->
            reduce(address, previous, snapshot)
        }
        .filterNotNull()

    private fun reduce(
        address: String,
        previous: TrackedDevice?,
        snapshot: ScanSnapshot,
    ): TrackedDevice? {
        val device = snapshot.devices.firstOrNull { it.address == address }
            ?: previous?.device?.asLost()
            ?: return null

        val trace = (previous?.rssiHistory.orEmpty() + device.smoothedRssi)
            .takeLast(tuning.traceCapacity)

        val stability = snapshot.focus
            ?.takeIf { it.address == address }
            ?.stability
            ?: previous?.stability
            ?: SignalStability.Unknown

        return TrackedDevice(
            device = device,
            trend = SignalTrend.of(
                oldestRssi = trace.first(),
                latestRssi = device.smoothedRssi,
                isLost = device.proximity.zone == ProximityZone.LOST,
            ),
            stability = stability,
            rssiHistory = trace,
        )
    }
}
