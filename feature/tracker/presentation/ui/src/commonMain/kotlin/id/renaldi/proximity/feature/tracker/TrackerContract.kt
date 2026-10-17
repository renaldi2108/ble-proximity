package id.renaldi.proximity.feature.tracker

import id.renaldi.proximity.core.common.mvi.UiEvent
import id.renaldi.proximity.core.designsystem.template.ScreenAction
import id.renaldi.proximity.core.designsystem.template.ScreenNavigation

sealed interface TrackerAction : ScreenAction, UiEvent {
    data object ToggleScan : TrackerAction
    data object Retry : TrackerAction
}

sealed interface TrackerNavigation : ScreenNavigation {
    data object Back : TrackerNavigation
}
