package id.renaldi.proximity.app.platform

import android.content.Context
import android.os.Build
import android.os.PowerManager
import id.renaldi.proximity.core.domain.port.ThermalMonitor
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf

class AndroidThermalMonitor(private val context: Context) : ThermalMonitor {
    override fun isThrottling(): Flow<Boolean> {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return flowOf(false)
        val power = context.getSystemService(PowerManager::class.java) ?: return flowOf(false)

        return callbackFlow {
            val listener = PowerManager.OnThermalStatusChangedListener { status ->
                trySend(status >= THROTTLE_THRESHOLD)
            }
            trySend(power.currentThermalStatus >= THROTTLE_THRESHOLD)
            power.addThermalStatusListener(listener)
            awaitClose { power.removeThermalStatusListener(listener) }
        }.distinctUntilChanged()
    }

    private companion object {
        const val THROTTLE_THRESHOLD = PowerManager.THERMAL_STATUS_SEVERE
    }
}
