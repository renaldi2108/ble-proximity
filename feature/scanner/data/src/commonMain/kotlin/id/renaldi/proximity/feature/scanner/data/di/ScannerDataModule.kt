package id.renaldi.proximity.feature.scanner.data.di

import id.renaldi.proximity.core.common.DiQualifiers
import id.renaldi.proximity.core.domain.port.ScanController
import id.renaldi.proximity.feature.scanner.data.BleScanController
import org.koin.core.qualifier.named
import org.koin.dsl.module

val scannerDataModule = module {
    single<ScanController> {
        BleScanController(
            scanner = get(),
            environment = get(),
            history = get(),
            thermal = get(),
            clock = get(),
            scope = get(named(DiQualifiers.APPLICATION_SCOPE)),
            dispatchers = get(),
        )
    }
}
