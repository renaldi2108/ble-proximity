package id.renaldi.proximity.core.ble.aggregate

import id.renaldi.proximity.core.model.BleAdvertisement
import id.renaldi.proximity.core.model.BleDevice
import id.renaldi.proximity.core.model.DeviceSighting
import id.renaldi.proximity.core.model.ScanTuning
import id.renaldi.proximity.core.model.SignalStability

class ScanAggregator(private val tuning: ScanTuning) {
    private val accumulators = LinkedHashMap<String, DeviceAccumulator>()

    val trackedCount: Int get() = accumulators.size

    fun record(advertisement: BleAdvertisement) {
        val existing = accumulators[advertisement.address]
        if (existing != null) {
            existing.accept(advertisement)
            return
        }
        if (accumulators.size >= tuning.maxDevices) evictOldest()
        accumulators
            .getOrPut(advertisement.address) {
                DeviceAccumulator(advertisement.address, tuning.rssiWindow)
            }
            .accept(advertisement)
    }

    fun snapshot(nowMillis: Long): List<BleDevice> {
        pruneStale(nowMillis)
        if (accumulators.isEmpty()) return emptyList()
        val devices = ArrayList<BleDevice>(accumulators.size)
        accumulators.values.forEach { devices += it.toDevice(nowMillis, tuning.lostAfterMillis) }
        return devices
    }

    fun stabilityOf(address: String, nowMillis: Long): SignalStability? =
        accumulators[address]?.stability(nowMillis)

    fun drainSightings(): List<DeviceSighting> {
        if (accumulators.isEmpty()) return emptyList()
        val pending = ArrayList<DeviceSighting>()
        accumulators.values.forEach { accumulator ->
            if (accumulator.hasUnpersistedPackets) {
                accumulator.drainSighting()?.let { pending += it }
            }
        }
        return pending
    }

    fun reset() = accumulators.clear()

    private fun pruneStale(nowMillis: Long) {
        if (accumulators.isEmpty()) return
        val iterator = accumulators.entries.iterator()
        while (iterator.hasNext()) {
            val accumulator = iterator.next().value
            val expired = nowMillis - accumulator.lastSeenMillis > tuning.staleAfterMillis
            if (expired && !accumulator.hasUnpersistedPackets) iterator.remove()
        }
    }

    private fun evictOldest() {
        var oldestAddress: String? = null
        var oldestSeen = Long.MAX_VALUE
        accumulators.forEach { (address, accumulator) ->
            if (accumulator.lastSeenMillis < oldestSeen) {
                oldestSeen = accumulator.lastSeenMillis
                oldestAddress = address
            }
        }
        oldestAddress?.let { accumulators.remove(it) }
    }
}
