package id.renaldi.proximity.feature.scanner.di

import id.renaldi.proximity.feature.scanner.ScannerViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val scannerUiModule = module {
    viewModel {
        ScannerViewModel(
            controller = get(),
            savedState = get(),
            observeVisibleDevices = get(),
            observeReadiness = get(),
        )
    }
}
