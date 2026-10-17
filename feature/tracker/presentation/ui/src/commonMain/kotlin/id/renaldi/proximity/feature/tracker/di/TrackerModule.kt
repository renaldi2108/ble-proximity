package id.renaldi.proximity.feature.tracker.di

import id.renaldi.proximity.feature.tracker.TrackerViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val trackerUiModule = module {
    viewModel { parameters ->
        TrackerViewModel(
            address = parameters.get(),
            controller = get(),
            observeTrackedDevice = get(),
            observeReadiness = get(),
        )
    }
}
