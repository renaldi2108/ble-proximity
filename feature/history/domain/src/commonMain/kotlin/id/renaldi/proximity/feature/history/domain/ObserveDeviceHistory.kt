package id.renaldi.proximity.feature.history.domain

import id.renaldi.proximity.core.domain.port.DeviceHistoryRepository
import id.renaldi.proximity.core.domain.usecase.FlowUseCase
import id.renaldi.proximity.core.model.DeviceHistoryEntry
import kotlinx.coroutines.flow.Flow

class ObserveDeviceHistory(
    private val repository: DeviceHistoryRepository,
) : FlowUseCase<List<DeviceHistoryEntry>> {
    override fun invoke(): Flow<List<DeviceHistoryEntry>> = repository.history()
}
