package id.renaldi.proximity.app.platform

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import id.renaldi.proximity.core.ble.BlePermissionChecker
import id.renaldi.proximity.core.domain.port.BleEnvironment
import id.renaldi.proximity.core.navigation.AppPlatformBridge
import org.koin.compose.koinInject

@Composable
actual fun rememberPlatformBridge(): AppPlatformBridge {
    val context = LocalContext.current
    val environment: BleEnvironment = koinInject()
    val checker: BlePermissionChecker = koinInject()
    val permissions = remember(checker) { checker.scanPermissions().toTypedArray() }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { environment.refresh() }

    val bluetoothLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { environment.refresh() }

    return remember(context, environment) {
        AppPlatformBridge(
            requestScanPermission = { permissionLauncher.launch(permissions) },
            enableBluetooth = {
                bluetoothLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            },
            openLocationSettings = {
                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            },
        )
    }
}
