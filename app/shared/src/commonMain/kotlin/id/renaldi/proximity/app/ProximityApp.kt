package id.renaldi.proximity.app

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.navigation.compose.NavHost
import id.renaldi.proximity.app.platform.rememberPlatformBridge
import id.renaldi.proximity.core.designsystem.foundation.ProximityTheme
import id.renaldi.proximity.core.domain.port.BleEnvironment
import id.renaldi.proximity.core.domain.port.ScanController
import id.renaldi.proximity.core.navigation.AppRoute
import id.renaldi.proximity.core.navigation.rememberAppState
import id.renaldi.proximity.feature.history.navigation.historyScreen
import id.renaldi.proximity.feature.scanner.navigation.scannerScreen
import id.renaldi.proximity.feature.tracker.navigation.trackerScreen
import org.koin.compose.koinInject

@Composable
fun ProximityApp() = ProximityTheme {
    val controller: ScanController = koinInject()
    val environment: BleEnvironment = koinInject()
    val app = rememberAppState(platform = rememberPlatformBridge())

    LifecycleStartEffect(controller) {
        environment.refresh()
        controller.onAppForegrounded()
        onStopOrDispose { controller.onAppBackgrounded() }
    }

    NavHost(navController = app.navController, startDestination = AppRoute.Scanner) {
        scannerScreen(app)
        trackerScreen(app)
        historyScreen(app)
    }
}
