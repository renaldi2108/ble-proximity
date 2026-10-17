package id.renaldi.proximity.feature.tracker

import id.renaldi.proximity.core.common.mvi.BaseViewModel
import id.renaldi.proximity.core.common.mvi.NoEffect
import id.renaldi.proximity.core.domain.port.ScanController
import id.renaldi.proximity.core.domain.usecase.ObserveBleReadiness
import id.renaldi.proximity.feature.tracker.domain.ObserveTrackedDevice
import id.renaldi.proximity.core.model.BleReadiness
import id.renaldi.proximity.core.model.ScanStatus

class TrackerViewModel(
    address: String,
    private val controller: ScanController,
    observeTrackedDevice: ObserveTrackedDevice,
    observeReadiness: ObserveBleReadiness,
) : BaseViewModel<TrackerUiState, TrackerAction, NoEffect>(TrackerUiState(address = address)) {
    init {
        controller.focus(address)

        observeTrackedDevice(address)
            .collectIntoStateWhileSubscribed { tracked -> copy(tracked = tracked) }

        observeReadiness().collectIntoState { readiness ->
            copy(readinessFailure = (readiness as? BleReadiness.Blocked)?.failure)
        }

        controller.status.collectIntoState { status ->
            copy(
                isScanning = status.isScanning,
                scanFailure = (status as? ScanStatus.Failed)?.failure,
            )
        }
    }

    override fun handleEvent(event: TrackerAction) {
        when (event) {
            TrackerAction.ToggleScan -> onToggleScan()
            TrackerAction.Retry -> controller.start()
        }
    }

    override fun onCleared() {
        controller.focus(null)
        super.onCleared()
    }

    private fun onToggleScan() {
        if (currentState.isScanning) controller.stop() else controller.start()
    }
}
