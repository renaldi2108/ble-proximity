package id.renaldi.proximity.feature.history.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import id.renaldi.proximity.core.designsystem.template.CollectEffects
import id.renaldi.proximity.core.navigation.AppRoute
import id.renaldi.proximity.core.navigation.AppState
import id.renaldi.proximity.feature.history.HistoryAction
import id.renaldi.proximity.feature.history.HistoryEffect
import id.renaldi.proximity.feature.history.HistoryNavigation
import id.renaldi.proximity.feature.history.HistoryScreen
import id.renaldi.proximity.feature.history.HistoryViewModel
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.historyScreen(app: AppState) {
    composable<AppRoute.History> {
        val viewModel: HistoryViewModel = koinViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val snackbarHostState = remember { SnackbarHostState() }

        CollectEffects(viewModel) { effect ->
            when (effect) {
                is HistoryEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
            }
        }

        LifecycleResumeEffect(Unit) {
            viewModel.onEvent(HistoryAction.ScreenResumed)
            onPauseOrDispose { }
        }

        HistoryScreen(
            state = state,
            onAction = viewModel::onEvent,
            onNavigate = { navigation ->
                when (navigation) {
                    HistoryNavigation.Back -> app.back()
                    is HistoryNavigation.OpenTracker ->
                        app.navigate(AppRoute.Tracker(navigation.address))
                }
            },
            snackbarHostState = snackbarHostState,
        )
    }
}
