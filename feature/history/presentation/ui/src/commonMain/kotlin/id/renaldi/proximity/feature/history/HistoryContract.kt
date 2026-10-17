package id.renaldi.proximity.feature.history

import id.renaldi.proximity.core.common.mvi.UiEffect
import id.renaldi.proximity.core.common.mvi.UiEvent
import id.renaldi.proximity.core.designsystem.template.ScreenAction
import id.renaldi.proximity.core.designsystem.template.ScreenNavigation

sealed interface HistoryAction : ScreenAction, UiEvent {
    data object ScreenResumed : HistoryAction
    data class Delete(val address: String) : HistoryAction
    data object RequestClear : HistoryAction
    data object ConfirmClear : HistoryAction
    data object DismissClear : HistoryAction
}

sealed interface HistoryNavigation : ScreenNavigation {
    data object Back : HistoryNavigation
    data class OpenTracker(val address: String) : HistoryNavigation
}

sealed interface HistoryEffect : UiEffect {
    data class ShowMessage(val message: String) : HistoryEffect
}
