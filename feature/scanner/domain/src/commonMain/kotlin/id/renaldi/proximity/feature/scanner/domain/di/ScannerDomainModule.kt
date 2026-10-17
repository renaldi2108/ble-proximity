package id.renaldi.proximity.feature.scanner.domain.di

import id.renaldi.proximity.feature.scanner.domain.ObserveVisibleDevices
import org.koin.dsl.module

val scannerDomainModule = module {
    factory { ObserveVisibleDevices(controller = get()) }
}
