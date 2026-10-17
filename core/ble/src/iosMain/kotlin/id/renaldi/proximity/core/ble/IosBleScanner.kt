package id.renaldi.proximity.core.ble

import id.renaldi.proximity.core.common.AppClock
import id.renaldi.proximity.core.domain.port.BleScanException
import id.renaldi.proximity.core.domain.port.BleScanner
import id.renaldi.proximity.core.model.BleAdvertisement
import id.renaldi.proximity.core.model.BleFailure
import id.renaldi.proximity.core.model.ScanTuning
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.CoreBluetooth.CBAdvertisementDataIsConnectable
import platform.CoreBluetooth.CBAdvertisementDataLocalNameKey
import platform.CoreBluetooth.CBAdvertisementDataTxPowerLevelKey
import platform.CoreBluetooth.CBCentralManager
import platform.CoreBluetooth.CBCentralManagerDelegateProtocol
import platform.CoreBluetooth.CBCentralManagerScanOptionAllowDuplicatesKey
import platform.CoreBluetooth.CBManagerStatePoweredOff
import platform.CoreBluetooth.CBManagerStatePoweredOn
import platform.CoreBluetooth.CBManagerStateUnauthorized
import platform.CoreBluetooth.CBManagerStateUnsupported
import platform.CoreBluetooth.CBPeripheral
import platform.Foundation.NSNumber
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
class IosBleScanner(private val clock: AppClock) : BleScanner {
    override fun advertisements(tuning: ScanTuning): Flow<BleAdvertisement> = callbackFlow {
        val delegate = object : NSObject(), CBCentralManagerDelegateProtocol {
            override fun centralManagerDidUpdateState(central: CBCentralManager) {
                when (central.state) {
                    CBManagerStatePoweredOn ->
                        central.scanForPeripheralsWithServices(null, ALLOW_DUPLICATES)

                    CBManagerStatePoweredOff -> close(BleScanException(BleFailure.BluetoothOff))
                    CBManagerStateUnauthorized ->
                        close(BleScanException(BleFailure.PermissionDenied))

                    CBManagerStateUnsupported -> close(BleScanException(BleFailure.Unsupported))
                    else -> Unit
                }
            }

            override fun centralManager(
                central: CBCentralManager,
                didDiscoverPeripheral: CBPeripheral,
                advertisementData: Map<Any?, *>,
                RSSI: NSNumber,
            ) {
                trySend(
                    BleAdvertisement(
                        address = didDiscoverPeripheral.identifier.UUIDString,
                        name = advertisementData.localName() ?: didDiscoverPeripheral.name,
                        rssi = RSSI.intValue,
                        txPower = advertisementData.number(CBAdvertisementDataTxPowerLevelKey)
                            ?.intValue,
                        isConnectable = advertisementData.number(CBAdvertisementDataIsConnectable)
                            ?.boolValue == true,
                        timestampMillis = clock.nowMillis(),
                    ),
                )
            }
        }

        val central = CBCentralManager(delegate, null)

        awaitClose {
            central.stopScan()
            central.delegate = null
        }
    }

    private companion object {
        val ALLOW_DUPLICATES: Map<Any?, Any> =
            mapOf(CBCentralManagerScanOptionAllowDuplicatesKey to true)
    }
}

private fun Map<Any?, *>.localName(): String? =
    get(CBAdvertisementDataLocalNameKey) as? String

private fun Map<Any?, *>.number(key: String): NSNumber? = get(key) as? NSNumber
