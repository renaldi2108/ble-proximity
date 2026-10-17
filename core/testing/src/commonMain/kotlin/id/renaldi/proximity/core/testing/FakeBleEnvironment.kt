package id.renaldi.proximity.core.testing

import id.renaldi.proximity.core.domain.port.BleEnvironment
import id.renaldi.proximity.core.model.BleReadiness
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.drop

class FakeBleEnvironment(initial: BleReadiness = BleReadiness.Ready) : BleEnvironment {
    private val state = MutableStateFlow(initial)

    var refreshCount: Int = 0
        private set

    override fun readiness(): BleReadiness = state.value

    override fun readinessChanges(): Flow<BleReadiness> = state.drop(1)

    override fun refresh() {
        refreshCount++
    }

    fun set(readiness: BleReadiness) {
        state.value = readiness
    }
}
