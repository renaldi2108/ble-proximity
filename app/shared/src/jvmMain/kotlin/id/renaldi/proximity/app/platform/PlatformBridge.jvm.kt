package id.renaldi.proximity.app.platform

import androidx.compose.runtime.Composable
import id.renaldi.proximity.core.navigation.AppPlatformBridge

@Composable
actual fun rememberPlatformBridge(): AppPlatformBridge = AppPlatformBridge.Inert
