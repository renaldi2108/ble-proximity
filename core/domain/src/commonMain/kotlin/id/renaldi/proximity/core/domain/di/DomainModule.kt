package id.renaldi.proximity.core.domain.di

import id.renaldi.proximity.core.domain.usecase.ObserveBleReadiness
import org.koin.dsl.module

val coreDomainModule = module {
    factory { ObserveBleReadiness(environment = get()) }
}
