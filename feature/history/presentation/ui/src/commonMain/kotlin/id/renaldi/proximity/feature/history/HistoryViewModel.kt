package id.renaldi.proximity.feature.history

import id.renaldi.proximity.core.common.AppClock
import id.renaldi.proximity.core.common.AppResult
import id.renaldi.proximity.core.common.mvi.BaseViewModel
import id.renaldi.proximity.feature.history.domain.ClearDeviceHistory
import id.renaldi.proximity.feature.history.domain.DeleteHistoryEntry
import id.renaldi.proximity.feature.history.domain.ObserveDeviceHistory

class HistoryViewModel(
    private val clock: AppClock,
    private val deleteEntry: DeleteHistoryEntry,
    private val clearHistory: ClearDeviceHistory,
    observeHistory: ObserveDeviceHistory,
) : BaseViewModel<HistoryUiState, HistoryAction, HistoryEffect>(
    HistoryUiState(nowMillis = clock.nowMillis()),
) {
    init {
        observeHistory().collectIntoStateWhileSubscribed { entries ->
            copy(
                nowMillis = clock.nowMillis(),
                content = if (entries.isEmpty()) {
                    HistoryContent.Empty
                } else {
                    HistoryContent.Entries(entries)
                },
            )
        }
    }

    override fun handleEvent(event: HistoryAction) {
        when (event) {
            HistoryAction.ScreenResumed -> setState { copy(nowMillis = clock.nowMillis()) }
            is HistoryAction.Delete -> onDelete(event.address)
            HistoryAction.RequestClear -> setState { copy(isClearDialogOpen = true) }
            HistoryAction.DismissClear -> setState { copy(isClearDialogOpen = false) }
            HistoryAction.ConfirmClear -> onConfirmClear()
        }
    }

    private fun onDelete(address: String) = launch {
        report(deleteEntry(address))
    }

    private fun onConfirmClear() = launch {
        setState { copy(isClearDialogOpen = false) }
        report(clearHistory())
    }

    private fun report(result: AppResult<Unit>) {
        if (result is AppResult.Failure) sendEffect(HistoryEffect.ShowMessage(result.message))
    }
}
