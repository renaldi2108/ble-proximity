package id.renaldi.proximity.app.di

import android.content.Context
import android.content.pm.PackageManager
import id.renaldi.proximity.app.platform.AndroidThermalMonitor
import id.renaldi.proximity.app.platform.isProbablyEmulator
import id.renaldi.proximity.core.ble.AndroidBleEnvironment
import id.renaldi.proximity.core.ble.AndroidBleScanner
import id.renaldi.proximity.core.ble.BlePermissionChecker
import id.renaldi.proximity.core.ble.simulation.SimulatedBleEnvironment
import id.renaldi.proximity.core.ble.simulation.SimulatedBleScanner
import id.renaldi.proximity.core.database.DatabaseDriverFactory
import id.renaldi.proximity.core.domain.port.BleEnvironment
import id.renaldi.proximity.core.domain.port.BleScanner
import id.renaldi.proximity.core.domain.port.ThermalMonitor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual fun platformModule(allowSimulatedScanner: Boolean): Module = module {
    single { DatabaseDriverFactory(androidContext()) }
    single { BlePermissionChecker(androidContext()) }
    single<ThermalMonitor> { AndroidThermalMonitor(androidContext()) }

    single<BleScanner> {
        if (androidContext().shouldSimulate(allowSimulatedScanner)) {
            SimulatedBleScanner(clock = get())
        } else {
            AndroidBleScanner(context = androidContext(), permissions = get())
        }
    }

    single<BleEnvironment> {
        if (androidContext().shouldSimulate(allowSimulatedScanner)) {
            SimulatedBleEnvironment
        } else {
            AndroidBleEnvironment(context = androidContext(), permissions = get())
        }
    }
}

private fun Context.shouldSimulate(allowSimulatedScanner: Boolean): Boolean {
    if (!allowSimulatedScanner) return false
    val hasLe = packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
    return isProbablyEmulator() || !hasLe
}
