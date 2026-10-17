package id.renaldi.proximity.feature.history.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import id.renaldi.proximity.core.common.DispatcherProvider
import id.renaldi.proximity.core.database.DatabaseFactory
import id.renaldi.proximity.core.database.DeviceHistory
import id.renaldi.proximity.core.database.ProximityDatabase
import id.renaldi.proximity.core.domain.port.DeviceHistoryRepository
import id.renaldi.proximity.core.model.DeviceHistoryEntry
import id.renaldi.proximity.core.model.DeviceSighting
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class DeviceHistoryRepositoryImpl(
    database: ProximityDatabase,
    private val dispatchers: DispatcherProvider,
) : DeviceHistoryRepository {
    private val queries = database.deviceHistoryQueries

    override fun history(): Flow<List<DeviceHistoryEntry>> = queries.selectAll()
        .asFlow()
        .mapToList(dispatchers.io)
        .map { rows -> rows.map(DeviceHistory::toEntry) }

    override fun entry(address: String): Flow<DeviceHistoryEntry?> = queries
        .selectByAddress(address)
        .asFlow()
        .mapToOneOrNull(dispatchers.io)
        .map { row -> row?.toEntry() }

    override suspend fun record(sightings: List<DeviceSighting>) {
        if (sightings.isEmpty()) return
        withContext(dispatchers.io) {
            queries.transaction {
                sightings.forEach(::write)
                queries.pruneBeyond(DatabaseFactory.HISTORY_ROW_LIMIT)
            }
        }
    }

    override suspend fun delete(address: String) {
        withContext(dispatchers.io) { queries.deleteByAddress(address) }
    }

    override suspend fun clear() {
        withContext(dispatchers.io) { queries.deleteAll() }
    }

    private fun write(sighting: DeviceSighting) {
        queries.insertOrIgnore(
            address = sighting.address,
            name = sighting.name,
            strongestRssi = sighting.strongestRssi.toLong(),
            lastRssi = sighting.lastRssi.toLong(),
            firstSeenAtMillis = sighting.firstSeenAtMillis,
            lastSeenAtMillis = sighting.lastSeenAtMillis,
        )
        queries.accumulate(
            name = sighting.name,
            strongestRssi = sighting.strongestRssi.toLong(),
            lastRssi = sighting.lastRssi.toLong(),
            firstSeenAtMillis = sighting.firstSeenAtMillis,
            lastSeenAtMillis = sighting.lastSeenAtMillis,
            packetDelta = sighting.packetDelta,
            address = sighting.address,
        )
    }
}

private fun DeviceHistory.toEntry() = DeviceHistoryEntry(
    address = address,
    name = name,
    strongestRssi = strongestRssi.toInt(),
    lastRssi = lastRssi.toInt(),
    firstSeenAtMillis = firstSeenAtMillis,
    lastSeenAtMillis = lastSeenAtMillis,
    sightingCount = sightingCount,
)
