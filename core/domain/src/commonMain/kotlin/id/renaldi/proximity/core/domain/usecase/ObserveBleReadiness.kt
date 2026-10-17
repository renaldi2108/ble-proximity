package id.renaldi.proximity.core.domain.usecase

import id.renaldi.proximity.core.domain.port.BleEnvironment
import id.renaldi.proximity.core.model.BleReadiness
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart

class ObserveBleReadiness(private val environment: BleEnvironment) : FlowUseCase<BleReadiness> {
    override fun invoke(): Flow<BleReadiness> = environment.readinessChanges()
        .onStart { emit(environment.readiness()) }
        .distinctUntilChanged()
}
