package id.renaldi.proximity.core.domain.port

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

fun interface ThermalMonitor {
    fun isThrottling(): Flow<Boolean>
}

object NoThermalMonitor : ThermalMonitor {
    override fun isThrottling(): Flow<Boolean> = flowOf(false)
}
