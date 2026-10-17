package id.renaldi.proximity.core.testing

import id.renaldi.proximity.core.domain.port.ScanController
import id.renaldi.proximity.core.model.BleDevice
import id.renaldi.proximity.core.model.DeviceFocus
import id.renaldi.proximity.core.model.ScanSnapshot
import id.renaldi.proximity.core.model.ScanStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeScanController : ScanController {
    private val statusState = MutableStateFlow<ScanStatus>(ScanStatus.Idle)
    override val status = statusState.asStateFlow()

    private val snapshotState = MutableStateFlow(ScanSnapshot.Empty)
    override val snapshots: Flow<ScanSnapshot> = snapshotState.asStateFlow()

    var startCount: Int = 0
        private set

    var stopCount: Int = 0
        private set

    var focusedAddress: String? = null
        private set

    override fun start() {
        startCount++
        statusState.value = ScanStatus.Scanning
    }

    override fun stop() {
        stopCount++
        statusState.value = ScanStatus.Idle
        snapshotState.value = ScanSnapshot.Empty
    }

    override fun focus(address: String?) {
        focusedAddress = address
    }

    override fun onAppForegrounded() = Unit

    override fun onAppBackgrounded() = Unit

    fun emit(devices: List<BleDevice>, atMillis: Long = 0L, focus: DeviceFocus? = null) {
        snapshotState.value = ScanSnapshot(devices, atMillis, focus)
    }

    fun setStatus(status: ScanStatus) {
        statusState.value = status
    }
}
