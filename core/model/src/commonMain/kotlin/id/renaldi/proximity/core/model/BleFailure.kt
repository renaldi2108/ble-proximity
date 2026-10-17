package id.renaldi.proximity.core.model

sealed interface BleFailure {
    data object Unsupported : BleFailure

    data object BluetoothOff : BleFailure

    data object PermissionDenied : BleFailure

    data object LocationServicesOff : BleFailure

    data class ScanRejected(val code: Int) : BleFailure

    data class Unexpected(val detail: String) : BleFailure
}
