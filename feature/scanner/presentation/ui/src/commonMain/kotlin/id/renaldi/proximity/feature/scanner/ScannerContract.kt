package id.renaldi.proximity.feature.scanner

import id.renaldi.proximity.core.common.mvi.UiEvent
import id.renaldi.proximity.core.designsystem.template.ScreenAction
import id.renaldi.proximity.core.designsystem.template.ScreenNavigation
import id.renaldi.proximity.core.model.DeviceSort

sealed interface ScannerAction : ScreenAction, UiEvent {
    data object ToggleScan : ScannerAction
    data object Retry : ScannerAction
    data class QueryChanged(val query: String) : ScannerAction
    data class ThresholdChanged(val minRssi: Int) : ScannerAction
    data class OnlyNamedChanged(val enabled: Boolean) : ScannerAction
    data class SortChanged(val sort: DeviceSort) : ScannerAction
    data object ResetFilter : ScannerAction
    data object OpenFilters : ScannerAction
    data object DismissFilters : ScannerAction
}

sealed interface ScannerNavigation : ScreenNavigation {
    data class OpenTracker(val address: String) : ScannerNavigation
    data object OpenHistory : ScannerNavigation
}
