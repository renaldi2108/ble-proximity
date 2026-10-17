package id.renaldi.proximity.core.model

sealed interface ScanStatus {
    data object Idle : ScanStatus

    data object Scanning : ScanStatus

    data class Failed(val failure: BleFailure) : ScanStatus

    val isScanning: Boolean get() = this is Scanning
}
