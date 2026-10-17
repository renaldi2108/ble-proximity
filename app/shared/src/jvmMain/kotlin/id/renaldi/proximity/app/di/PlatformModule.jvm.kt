package id.renaldi.proximity.app.di

import id.renaldi.proximity.core.ble.simulation.SimulatedBleEnvironment
import id.renaldi.proximity.core.ble.simulation.SimulatedBleScanner
import id.renaldi.proximity.core.database.DatabaseDriverFactory
import id.renaldi.proximity.core.domain.port.BleEnvironment
import id.renaldi.proximity.core.domain.port.BleScanner
import id.renaldi.proximity.core.domain.port.NoThermalMonitor
import id.renaldi.proximity.core.domain.port.ThermalMonitor
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual fun platformModule(allowSimulatedScanner: Boolean): Module = module {
    single { DatabaseDriverFactory() }
    single<ThermalMonitor> { NoThermalMonitor }
    single<BleScanner> { SimulatedBleScanner(clock = get()) }
    single<BleEnvironment> { SimulatedBleEnvironment }
}
