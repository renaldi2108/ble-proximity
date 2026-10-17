package id.renaldi.proximity.core.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.SystemClock
import android.util.Log
import id.renaldi.proximity.core.domain.port.BleScanException
import id.renaldi.proximity.core.domain.port.BleScanner
import id.renaldi.proximity.core.model.BleAdvertisement
import id.renaldi.proximity.core.model.BleFailure
import id.renaldi.proximity.core.model.ScanPower
import id.renaldi.proximity.core.model.ScanTuning
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AndroidBleScanner(
    private val context: Context,
    private val permissions: BlePermissionChecker,
) : BleScanner {
    @SuppressLint("MissingPermission")
    override fun advertisements(tuning: ScanTuning): Flow<BleAdvertisement> = callbackFlow {
        val adapter = context.bluetoothAdapter() ?: fail(BleFailure.Unsupported)
        if (!adapter.isEnabled) fail(BleFailure.BluetoothOff)
        if (!permissions.hasScanPermission()) fail(BleFailure.PermissionDenied)
        if (!permissions.isLocationUsable()) fail(BleFailure.LocationServicesOff)
        val scanner = adapter.bluetoothLeScanner ?: fail(BleFailure.BluetoothOff)

        val bootOffsetMillis = System.currentTimeMillis() - SystemClock.elapsedRealtime()

        val callback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                trySend(result.toAdvertisement(bootOffsetMillis))
            }

            override fun onBatchScanResults(results: MutableList<ScanResult>) {
                results.forEach { trySend(it.toAdvertisement(bootOffsetMillis)) }
            }

            override fun onScanFailed(errorCode: Int) {
                close(BleScanException(BleFailure.ScanRejected(errorCode)))
            }
        }

        try {
            scanner.startScan(null, tuning.toScanSettings(adapter), callback)
        } catch (denied: SecurityException) {
            close(BleScanException(BleFailure.PermissionDenied))
        }

        awaitClose { scanner.stopScanQuietly(callback) }
    }
}

@SuppressLint("MissingPermission")
private fun BluetoothLeScanner.stopScanQuietly(callback: ScanCallback) {
    try {
        stopScan(callback)
    } catch (denied: SecurityException) {
        failSilently(denied)
    } catch (adapterOff: IllegalStateException) {
        failSilently(adapterOff)
    }
}

private fun failSilently(cause: Throwable) {
    Log.d("AndroidBleScanner", "stopScan ignored: ${cause.message}")
}

private fun fail(failure: BleFailure): Nothing = throw BleScanException(failure)

private fun ScanResult.toAdvertisement(bootOffsetMillis: Long) = BleAdvertisement(
    address = device.address,
    name = scanRecord?.deviceName?.takeIf(String::isNotBlank),
    rssi = rssi,
    txPower = txPower.takeIf { it != ScanResult.TX_POWER_NOT_PRESENT },
    isConnectable = isConnectable,
    timestampMillis = bootOffsetMillis + timestampNanos / NANOS_PER_MILLI,
)

private fun ScanTuning.toScanSettings(adapter: BluetoothAdapter): ScanSettings {
    val builder = ScanSettings.Builder()
        .setScanMode(power.toScanMode())
        .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
        .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
        .setNumOfMatches(ScanSettings.MATCH_NUM_MAX_ADVERTISEMENT)
        .setReportDelay(0L)
    if (adapter.isLeExtendedAdvertisingSupported) {
        builder.setLegacy(false).setPhy(ScanSettings.PHY_LE_ALL_SUPPORTED)
    }
    return builder.build()
}

private fun ScanPower.toScanMode(): Int = when (this) {
    ScanPower.LOW_POWER -> ScanSettings.SCAN_MODE_LOW_POWER
    ScanPower.BALANCED -> ScanSettings.SCAN_MODE_BALANCED
    ScanPower.LOW_LATENCY -> ScanSettings.SCAN_MODE_LOW_LATENCY
}

private const val NANOS_PER_MILLI = 1_000_000L
