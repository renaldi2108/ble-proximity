package id.renaldi.proximity.feature.history.domain.di

import id.renaldi.proximity.feature.history.domain.ClearDeviceHistory
import id.renaldi.proximity.feature.history.domain.DeleteHistoryEntry
import id.renaldi.proximity.feature.history.domain.ObserveDeviceHistory
import org.koin.dsl.module

val historyDomainModule = module {
    factory { ObserveDeviceHistory(repository = get()) }
    factory { DeleteHistoryEntry(repository = get()) }
    factory { ClearDeviceHistory(repository = get()) }
}
