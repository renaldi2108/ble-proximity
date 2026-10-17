package id.renaldi.proximity.feature.tracker.domain.di

import id.renaldi.proximity.feature.tracker.domain.ObserveTrackedDevice
import org.koin.dsl.module

val trackerDomainModule = module {
    factory { ObserveTrackedDevice(controller = get()) }
}
