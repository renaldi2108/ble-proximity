package id.renaldi.proximity.feature.history.domain

import id.renaldi.proximity.core.common.AppResult
import id.renaldi.proximity.core.common.appRunCatching
import id.renaldi.proximity.core.domain.port.DeviceHistoryRepository

class DeleteHistoryEntry(private val repository: DeviceHistoryRepository) {
    suspend operator fun invoke(address: String): AppResult<Unit> =
        appRunCatching(FAILURE_MESSAGE) { repository.delete(address) }

    private companion object {
        const val FAILURE_MESSAGE = "Gagal menghapus riwayat perangkat"
    }
}

class ClearDeviceHistory(private val repository: DeviceHistoryRepository) {
    suspend operator fun invoke(): AppResult<Unit> =
        appRunCatching(FAILURE_MESSAGE) { repository.clear() }

    private companion object {
        const val FAILURE_MESSAGE = "Gagal mengosongkan riwayat perangkat"
    }
}
