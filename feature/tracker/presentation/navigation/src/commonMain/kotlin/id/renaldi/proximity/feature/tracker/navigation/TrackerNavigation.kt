package id.renaldi.proximity.feature.tracker.navigation

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import id.renaldi.proximity.core.navigation.AppRoute
import id.renaldi.proximity.core.navigation.AppState
import id.renaldi.proximity.feature.tracker.TrackerNavigation
import id.renaldi.proximity.feature.tracker.TrackerScreen
import id.renaldi.proximity.feature.tracker.TrackerViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

fun NavGraphBuilder.trackerScreen(app: AppState) {
    composable<AppRoute.Tracker> { entry ->
        val route: AppRoute.Tracker = entry.toRoute()
        val viewModel: TrackerViewModel = koinViewModel { parametersOf(route.address) }
        val state by viewModel.state.collectAsStateWithLifecycle()

        TrackerScreen(
            state = state,
            onAction = viewModel::onEvent,
            onNavigate = { navigation ->
                when (navigation) {
                    TrackerNavigation.Back -> app.back()
                }
            },
            onEnableBluetooth = app.platform.enableBluetooth,
            onRequestPermission = app.platform.requestScanPermission,
            onOpenLocationSettings = app.platform.openLocationSettings,
        )
    }
}
