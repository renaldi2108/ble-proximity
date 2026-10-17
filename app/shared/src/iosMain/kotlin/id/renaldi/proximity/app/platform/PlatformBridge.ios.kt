package id.renaldi.proximity.app.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import id.renaldi.proximity.core.navigation.AppPlatformBridge
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

@Composable
actual fun rememberPlatformBridge(): AppPlatformBridge = remember {
    AppPlatformBridge(
        requestScanPermission = ::openSystemSettings,
        enableBluetooth = ::openSystemSettings,
        openLocationSettings = ::openSystemSettings,
    )
}

private fun openSystemSettings() {
    val url = NSURL.URLWithString(UIApplicationOpenSettingsURLString) ?: return
    UIApplication.sharedApplication.openURL(url)
}
