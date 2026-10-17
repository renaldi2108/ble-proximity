package id.renaldi.proximity.app

import androidx.compose.ui.window.ComposeUIViewController
import id.renaldi.proximity.app.di.configureAppModules
import org.koin.core.context.startKoin
import platform.UIKit.UIViewController

fun startProximityKoin(allowSimulatedScanner: Boolean) {
    startKoin {
        configureAppModules(allowSimulatedScanner = allowSimulatedScanner)
    }
}

fun ProximityViewController(): UIViewController = ComposeUIViewController { ProximityApp() }
