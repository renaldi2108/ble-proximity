package id.renaldi.proximity.feature.scanner.domain

import id.renaldi.proximity.core.domain.port.ScanController
import id.renaldi.proximity.core.model.BleDevice
import id.renaldi.proximity.core.model.DeviceFilter
import id.renaldi.proximity.core.model.DeviceListResult
import id.renaldi.proximity.core.model.DeviceSort
import id.renaldi.proximity.core.model.ScanSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ObserveVisibleDevices(private val controller: ScanController) {
    operator fun invoke(filters: Flow<DeviceFilter>): Flow<DeviceListResult> =
        combine(controller.snapshots, filters, ::apply)

    private fun apply(snapshot: ScanSnapshot, filter: DeviceFilter): DeviceListResult {
        val matching = snapshot.devices.filterTo(ArrayList(snapshot.devices.size), filter::matches)
        matching.sortWith(filter.sort.comparator())
        return DeviceListResult(
            devices = matching,
            visibleCount = matching.size,
            totalCount = snapshot.devices.size,
            capturedAtMillis = snapshot.capturedAtMillis,
        )
    }

    private fun DeviceSort.comparator(): Comparator<BleDevice> = when (this) {
        DeviceSort.STRONGEST_SIGNAL -> STRONGEST_FIRST
        DeviceSort.RECENTLY_SEEN -> RECENT_FIRST
        DeviceSort.NAME -> BY_NAME
    }

    private companion object {
        val STRONGEST_FIRST: Comparator<BleDevice> =
            compareByDescending<BleDevice> { it.smoothedRssi }.thenBy { it.address }

        val RECENT_FIRST: Comparator<BleDevice> =
            compareByDescending<BleDevice> { it.lastSeenAtMillis }.thenBy { it.address }

        val BY_NAME = Comparator<BleDevice> { left, right ->
            val namedFirst = left.hasName.rank().compareTo(right.hasName.rank())
            if (namedFirst != 0) return@Comparator namedFirst
            val byName = left.displayName.compareTo(right.displayName, ignoreCase = true)
            if (byName != 0) byName else left.address.compareTo(right.address)
        }

        private fun Boolean.rank(): Int = if (this) 0 else 1
    }
}
