package id.renaldi.proximity.core.domain.port

import id.renaldi.proximity.core.model.DeviceHistoryEntry
import id.renaldi.proximity.core.model.DeviceSighting
import kotlinx.coroutines.flow.Flow

interface DeviceHistoryRepository {
    fun history(): Flow<List<DeviceHistoryEntry>>

    fun entry(address: String): Flow<DeviceHistoryEntry?>

    suspend fun record(sightings: List<DeviceSighting>)

    suspend fun delete(address: String)

    suspend fun clear()
}
