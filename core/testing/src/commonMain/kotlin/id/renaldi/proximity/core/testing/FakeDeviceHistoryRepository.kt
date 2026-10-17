package id.renaldi.proximity.core.testing

import id.renaldi.proximity.core.domain.port.DeviceHistoryRepository
import id.renaldi.proximity.core.model.DeviceHistoryEntry
import id.renaldi.proximity.core.model.DeviceSighting
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeDeviceHistoryRepository(
    initial: List<DeviceHistoryEntry> = emptyList(),
) : DeviceHistoryRepository {
    private val entries = MutableStateFlow(initial.associateBy(DeviceHistoryEntry::address))

    private val recordedBatches = MutableStateFlow<List<List<DeviceSighting>>>(emptyList())

    val batches: List<List<DeviceSighting>> get() = recordedBatches.value

    var failOnWrite: Boolean = false

    override fun history(): Flow<List<DeviceHistoryEntry>> =
        entries.map { it.values.sortedByDescending(DeviceHistoryEntry::lastSeenAtMillis) }

    override fun entry(address: String): Flow<DeviceHistoryEntry?> = entries.map { it[address] }

    override suspend fun record(sightings: List<DeviceSighting>) {
        if (failOnWrite) error("history write rejected")
        if (sightings.isEmpty()) return
        recordedBatches.value = recordedBatches.value + listOf(sightings)
        entries.value = entries.value.toMutableMap().apply {
            sightings.forEach { sighting -> put(sighting.address, sighting.merge(this[sighting.address])) }
        }
    }

    override suspend fun delete(address: String) {
        if (failOnWrite) error("history delete rejected")
        entries.value = entries.value - address
    }

    override suspend fun clear() {
        if (failOnWrite) error("history clear rejected")
        entries.value = emptyMap()
    }
}

private fun DeviceSighting.merge(existing: DeviceHistoryEntry?) = DeviceHistoryEntry(
    address = address,
    name = name ?: existing?.name,
    strongestRssi = maxOf(strongestRssi, existing?.strongestRssi ?: strongestRssi),
    lastRssi = lastRssi,
    firstSeenAtMillis = minOf(firstSeenAtMillis, existing?.firstSeenAtMillis ?: firstSeenAtMillis),
    lastSeenAtMillis = maxOf(lastSeenAtMillis, existing?.lastSeenAtMillis ?: lastSeenAtMillis),
    sightingCount = (existing?.sightingCount ?: 0L) + packetDelta,
)
