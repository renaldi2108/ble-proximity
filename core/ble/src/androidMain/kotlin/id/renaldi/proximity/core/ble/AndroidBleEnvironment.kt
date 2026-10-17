package id.renaldi.proximity.core.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import id.renaldi.proximity.core.domain.port.BleEnvironment
import id.renaldi.proximity.core.model.BleFailure
import id.renaldi.proximity.core.model.BleReadiness
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

class AndroidBleEnvironment(
    private val context: Context,
    private val permissions: BlePermissionChecker,
) : BleEnvironment {
    override fun readiness(): BleReadiness {
        val supportsLe = context.packageManager
            .hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
        if (!supportsLe) return BleReadiness.Blocked(BleFailure.Unsupported)

        val adapter = context.bluetoothAdapter()
            ?: return BleReadiness.Blocked(BleFailure.Unsupported)
        if (!adapter.isEnabled) return BleReadiness.Blocked(BleFailure.BluetoothOff)
        if (!permissions.hasScanPermission()) {
            return BleReadiness.Blocked(BleFailure.PermissionDenied)
        }
        if (!permissions.isLocationUsable()) {
            return BleReadiness.Blocked(BleFailure.LocationServicesOff)
        }
        return BleReadiness.Ready
    }

    override fun readinessChanges(): Flow<BleReadiness> = merge(systemChanges(), refreshes)
        .map { readiness() }
        .distinctUntilChanged()

    override fun refresh() {
        refreshes.tryEmit(Unit)
    }

    private fun systemChanges(): Flow<Unit> = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                trySend(Unit)
            }
        }
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED).apply {
            addAction(LocationManager.PROVIDERS_CHANGED_ACTION)
        }
        context.registerSystemReceiver(receiver, filter)
        awaitClose { context.unregisterReceiver(receiver) }
    }

    private companion object {
        val refreshes = MutableSharedFlow<Unit>(
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )
    }
}

internal fun Context.bluetoothAdapter(): BluetoothAdapter? =
    getSystemService(BluetoothManager::class.java)?.adapter

private fun Context.registerSystemReceiver(receiver: BroadcastReceiver, filter: IntentFilter) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
    } else {
        registerReceiver(receiver, filter)
    }
}
