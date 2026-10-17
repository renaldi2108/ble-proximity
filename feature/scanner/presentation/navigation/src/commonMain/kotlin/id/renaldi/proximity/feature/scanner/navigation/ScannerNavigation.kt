package id.renaldi.proximity.feature.scanner.navigation

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import id.renaldi.proximity.core.navigation.AppRoute
import id.renaldi.proximity.core.navigation.AppState
import id.renaldi.proximity.feature.scanner.ScannerNavigation
import id.renaldi.proximity.feature.scanner.ScannerScreen
import id.renaldi.proximity.feature.scanner.ScannerViewModel
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.scannerScreen(app: AppState) {
    composable<AppRoute.Scanner> {
        val viewModel: ScannerViewModel = koinViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()

        ScannerScreen(
            state = state,
            onAction = viewModel::onEvent,
            onNavigate = { navigation ->
                when (navigation) {
                    is ScannerNavigation.OpenTracker ->
                        app.navigate(AppRoute.Tracker(navigation.address))

                    ScannerNavigation.OpenHistory -> app.navigate(AppRoute.History)
                }
            },
            onEnableBluetooth = app.platform.enableBluetooth,
            onRequestPermission = app.platform.requestScanPermission,
            onOpenLocationSettings = app.platform.openLocationSettings,
        )
    }
}
