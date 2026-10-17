package id.renaldi.proximity.core.domain.port

import id.renaldi.proximity.core.model.ScanSnapshot
import id.renaldi.proximity.core.model.ScanStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ScanController {
    val status: StateFlow<ScanStatus>

    val snapshots: Flow<ScanSnapshot>

    fun start()

    fun stop()

    fun focus(address: String?)

    fun onAppForegrounded()

    fun onAppBackgrounded()
}
