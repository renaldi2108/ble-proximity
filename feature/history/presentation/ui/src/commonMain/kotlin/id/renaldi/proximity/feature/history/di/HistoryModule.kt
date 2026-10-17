package id.renaldi.proximity.feature.history.di

import id.renaldi.proximity.feature.history.HistoryViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val historyUiModule = module {
    viewModel {
        HistoryViewModel(
            clock = get(),
            deleteEntry = get(),
            clearHistory = get(),
            observeHistory = get(),
        )
    }
}
