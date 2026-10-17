package id.renaldi.proximity.app.di

import id.renaldi.proximity.core.common.AppClock
import id.renaldi.proximity.core.common.DiQualifiers
import id.renaldi.proximity.core.common.DispatcherProvider
import id.renaldi.proximity.core.common.PlatformDispatchers
import id.renaldi.proximity.core.common.SystemAppClock
import id.renaldi.proximity.core.database.DatabaseDriverFactory
import id.renaldi.proximity.core.database.DatabaseFactory
import id.renaldi.proximity.core.domain.di.coreDomainModule
import id.renaldi.proximity.feature.history.data.di.historyDataModule
import id.renaldi.proximity.feature.history.di.historyUiModule
import id.renaldi.proximity.feature.history.domain.di.historyDomainModule
import id.renaldi.proximity.feature.scanner.data.di.scannerDataModule
import id.renaldi.proximity.feature.scanner.di.scannerUiModule
import id.renaldi.proximity.feature.scanner.domain.di.scannerDomainModule
import id.renaldi.proximity.feature.tracker.di.trackerUiModule
import id.renaldi.proximity.feature.tracker.domain.di.trackerDomainModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val coreModule = module {
    single<AppClock> { SystemAppClock }
    single<DispatcherProvider> { PlatformDispatchers }

    single(named(DiQualifiers.APPLICATION_SCOPE)) {
        CoroutineScope(SupervisorJob() + get<DispatcherProvider>().default)
    }

    single { DatabaseFactory.create(get<DatabaseDriverFactory>().create()) }
}

fun KoinApplication.configureAppModules(allowSimulatedScanner: Boolean) {
    modules(
        coreModule,
        coreDomainModule,
        scannerDomainModule,
        scannerDataModule,
        scannerUiModule,
        trackerDomainModule,
        trackerUiModule,
        historyDomainModule,
        historyDataModule,
        historyUiModule,
        platformModule(allowSimulatedScanner),
    )
}

internal expect fun platformModule(allowSimulatedScanner: Boolean): Module
