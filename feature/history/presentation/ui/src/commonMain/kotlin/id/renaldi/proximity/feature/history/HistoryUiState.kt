package id.renaldi.proximity.feature.history

import id.renaldi.proximity.core.common.mvi.UiState
import id.renaldi.proximity.core.model.DeviceHistoryEntry

sealed interface HistoryContent {
    data object Loading : HistoryContent

    data object Empty : HistoryContent

    data class Entries(val entries: List<DeviceHistoryEntry>) : HistoryContent
}

data class HistoryUiState(
    val nowMillis: Long,
    val content: HistoryContent = HistoryContent.Loading,
    val isClearDialogOpen: Boolean = false,
) : UiState {
    val count: Int get() = (content as? HistoryContent.Entries)?.entries?.size ?: 0

    val canClear: Boolean get() = content is HistoryContent.Entries
}
