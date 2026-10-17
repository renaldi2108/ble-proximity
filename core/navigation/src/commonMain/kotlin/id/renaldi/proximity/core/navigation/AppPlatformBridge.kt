package id.renaldi.proximity.core.navigation

import androidx.compose.runtime.Stable

@Stable
class AppPlatformBridge(
    val requestScanPermission: () -> Unit,
    val enableBluetooth: () -> Unit,
    val openLocationSettings: () -> Unit,
) {
    companion object {
        val Inert = AppPlatformBridge(
            requestScanPermission = {},
            enableBluetooth = {},
            openLocationSettings = {},
        )
    }
}
