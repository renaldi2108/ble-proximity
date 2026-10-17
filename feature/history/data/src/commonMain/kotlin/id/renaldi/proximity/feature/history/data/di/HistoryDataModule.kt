package id.renaldi.proximity.feature.history.data.di

import id.renaldi.proximity.core.domain.port.DeviceHistoryRepository
import id.renaldi.proximity.feature.history.data.DeviceHistoryRepositoryImpl
import org.koin.dsl.module

val historyDataModule = module {
    single<DeviceHistoryRepository> {
        DeviceHistoryRepositoryImpl(database = get(), dispatchers = get())
    }
}
