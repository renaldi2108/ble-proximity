package id.renaldi.proximity.core.ble

import id.renaldi.proximity.core.domain.port.BleEnvironment
import id.renaldi.proximity.core.model.BleFailure
import id.renaldi.proximity.core.model.BleReadiness
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.CoreBluetooth.CBCentralManager
import platform.CoreBluetooth.CBCentralManagerDelegateProtocol
import platform.CoreBluetooth.CBManagerState
import platform.CoreBluetooth.CBManagerStatePoweredOff
import platform.CoreBluetooth.CBManagerStatePoweredOn
import platform.CoreBluetooth.CBManagerStateUnauthorized
import platform.CoreBluetooth.CBManagerStateUnsupported
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
class IosBleEnvironment : BleEnvironment {
    private val readinessState = MutableStateFlow<BleReadiness>(PENDING)

    private val delegate = object : NSObject(), CBCentralManagerDelegateProtocol {
        override fun centralManagerDidUpdateState(central: CBCentralManager) {
            readinessState.value = central.state.toReadiness()
        }
    }

    private val central = CBCentralManager(delegate, null)

    override fun readiness(): BleReadiness = central.state.toReadiness()

    override fun readinessChanges(): Flow<BleReadiness> = readinessState.asStateFlow()

    override fun refresh() {
        readinessState.value = central.state.toReadiness()
    }

    private companion object {
        val PENDING = BleReadiness.Blocked(BleFailure.Unexpected("Menunggu status Bluetooth"))
    }
}

private fun CBManagerState.toReadiness(): BleReadiness = when (this) {
    CBManagerStatePoweredOn -> BleReadiness.Ready
    CBManagerStatePoweredOff -> BleReadiness.Blocked(BleFailure.BluetoothOff)
    CBManagerStateUnauthorized -> BleReadiness.Blocked(BleFailure.PermissionDenied)
    CBManagerStateUnsupported -> BleReadiness.Blocked(BleFailure.Unsupported)
    else -> BleReadiness.Blocked(BleFailure.Unexpected("Bluetooth belum siap"))
}
