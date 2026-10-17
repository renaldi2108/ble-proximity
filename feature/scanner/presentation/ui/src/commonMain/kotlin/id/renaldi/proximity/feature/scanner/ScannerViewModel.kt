package id.renaldi.proximity.feature.scanner

import androidx.lifecycle.SavedStateHandle
import id.renaldi.proximity.core.common.mvi.BaseViewModel
import id.renaldi.proximity.core.common.mvi.NoEffect
import id.renaldi.proximity.core.domain.port.ScanController
import id.renaldi.proximity.core.domain.usecase.ObserveBleReadiness
import id.renaldi.proximity.feature.scanner.domain.ObserveVisibleDevices
import id.renaldi.proximity.core.model.BleReadiness
import id.renaldi.proximity.core.model.DeviceFilter
import id.renaldi.proximity.core.model.DeviceListResult
import id.renaldi.proximity.core.model.DeviceSort
import id.renaldi.proximity.core.model.ScanStatus
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class ScannerViewModel(
    private val controller: ScanController,
    private val savedState: SavedStateHandle,
    observeVisibleDevices: ObserveVisibleDevices,
    observeReadiness: ObserveBleReadiness,
) : BaseViewModel<ScannerUiState, ScannerAction, NoEffect>(
    ScannerUiState(filter = savedState.readFilter()),
) {
    init {
        observeVisibleDevices(state.map { it.filter }.distinctUntilChanged())
            .collectIntoStateWhileSubscribed { result ->
                copy(content = result.asContent(filter, isScanning))
            }

        observeReadiness().collectIntoState { readiness ->
            copy(readinessFailure = (readiness as? BleReadiness.Blocked)?.failure)
        }

        controller.status.collectIntoState { status ->
            copy(
                isScanning = status.isScanning,
                scanFailure = (status as? ScanStatus.Failed)?.failure,
                content = if (status is ScanStatus.Idle) ScannerContent.Idle else content,
            )
        }
    }

    override fun handleEvent(event: ScannerAction) {
        when (event) {
            ScannerAction.ToggleScan -> onToggleScan()
            ScannerAction.Retry -> controller.start()
            is ScannerAction.QueryChanged -> updateFilter { copy(query = event.query) }
            is ScannerAction.ThresholdChanged -> updateFilter { copy(minRssi = event.minRssi) }
            is ScannerAction.OnlyNamedChanged -> updateFilter { copy(onlyNamed = event.enabled) }
            is ScannerAction.SortChanged -> updateFilter { copy(sort = event.sort) }
            ScannerAction.ResetFilter -> updateFilter { DeviceFilter.Default }
            ScannerAction.OpenFilters -> setState { copy(areFiltersOpen = true) }
            ScannerAction.DismissFilters -> setState { copy(areFiltersOpen = false) }
        }
    }

    private fun onToggleScan() {
        if (currentState.isScanning) controller.stop() else controller.start()
    }

    private fun updateFilter(reducer: DeviceFilter.() -> DeviceFilter) {
        val next = currentState.filter.reducer()
        savedState.writeFilter(next)
        setState { copy(filter = next) }
    }
}

private fun DeviceListResult.asContent(
    filter: DeviceFilter,
    isScanning: Boolean,
): ScannerContent = when {
    devices.isNotEmpty() -> ScannerContent.Found(this)
    totalCount > 0 && filter.isActive -> ScannerContent.NoMatch(totalCount)
    isScanning -> ScannerContent.Searching
    else -> ScannerContent.Idle
}

private const val KEY_QUERY = "scanner.query"
private const val KEY_MIN_RSSI = "scanner.minRssi"
private const val KEY_ONLY_NAMED = "scanner.onlyNamed"
private const val KEY_SORT = "scanner.sort"

private fun SavedStateHandle.readFilter() = DeviceFilter(
    query = get<String>(KEY_QUERY) ?: "",
    minRssi = get<Int>(KEY_MIN_RSSI) ?: DeviceFilter.NO_THRESHOLD,
    onlyNamed = get<Boolean>(KEY_ONLY_NAMED) ?: false,
    sort = get<String>(KEY_SORT)?.let(DeviceSort::valueOf) ?: DeviceSort.STRONGEST_SIGNAL,
)

private fun SavedStateHandle.writeFilter(filter: DeviceFilter) {
    set(KEY_QUERY, filter.query)
    set(KEY_MIN_RSSI, filter.minRssi)
    set(KEY_ONLY_NAMED, filter.onlyNamed)
    set(KEY_SORT, filter.sort.name)
}
