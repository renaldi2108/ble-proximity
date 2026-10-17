package id.renaldi.proximity.feature.scanner

import id.renaldi.proximity.core.common.mvi.UiState
import id.renaldi.proximity.core.model.BleFailure
import id.renaldi.proximity.core.model.DeviceFilter
import id.renaldi.proximity.core.model.DeviceListResult

sealed interface ScannerContent {
    data object Idle : ScannerContent

    data object Searching : ScannerContent

    data class Found(val result: DeviceListResult) : ScannerContent

    data class NoMatch(val totalCount: Int) : ScannerContent
}

data class ScannerUiState(
    val content: ScannerContent = ScannerContent.Idle,
    val filter: DeviceFilter = DeviceFilter.Default,
    val isScanning: Boolean = false,
    val scanFailure: BleFailure? = null,
    val readinessFailure: BleFailure? = null,
    val areFiltersOpen: Boolean = false,
) : UiState {
    val blocker: BleFailure? get() = scanFailure ?: readinessFailure

    val canScan: Boolean get() = readinessFailure == null

    val visibleCount: Int get() = (content as? ScannerContent.Found)?.result?.visibleCount ?: 0

    val hiddenCount: Int
        get() = when (content) {
            is ScannerContent.Found -> content.result.hiddenCount
            is ScannerContent.NoMatch -> content.totalCount
            else -> 0
        }
}
