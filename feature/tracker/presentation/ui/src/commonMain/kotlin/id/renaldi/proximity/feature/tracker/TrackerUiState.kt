package id.renaldi.proximity.feature.tracker

import id.renaldi.proximity.core.common.mvi.UiState
import id.renaldi.proximity.core.model.BleFailure
import id.renaldi.proximity.core.model.ProximityZone
import id.renaldi.proximity.core.model.TrackedDevice

data class TrackerUiState(
    val address: String,
    val tracked: TrackedDevice? = null,
    val isScanning: Boolean = false,
    val scanFailure: BleFailure? = null,
    val readinessFailure: BleFailure? = null,
) : UiState {
    val blocker: BleFailure? get() = scanFailure ?: readinessFailure

    val title: String get() = tracked?.device?.displayName ?: address

    val zone: ProximityZone get() = tracked?.device?.proximity?.zone ?: ProximityZone.LOST

    val isAcquiring: Boolean get() = tracked == null
}
