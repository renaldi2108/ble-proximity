package id.renaldi.proximity.core.ble.simulation

import id.renaldi.proximity.core.common.AppClock
import id.renaldi.proximity.core.domain.port.BleEnvironment
import id.renaldi.proximity.core.domain.port.BleScanner
import id.renaldi.proximity.core.model.BleAdvertisement
import id.renaldi.proximity.core.model.BleReadiness
import id.renaldi.proximity.core.model.RssiCalibration
import id.renaldi.proximity.core.model.ScanTuning
import kotlin.math.log10
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow

class SimulatedBleScanner(
    private val clock: AppClock,
    private val profile: SimulationProfile = SimulationProfile.Default,
) : BleScanner {
    override fun advertisements(tuning: ScanTuning): Flow<BleAdvertisement> = flow {
        val random = Random(profile.seed)
        val walkers = profile.devices.map(::Walker)
        while (true) {
            delay(profile.packetIntervalMillis)
            val walker = walkers[random.nextInt(walkers.size)]
            emit(walker.advance(clock.nowMillis(), random))
        }
    }

    private class Walker(private val spec: SimulatedDevice) {
        private var meters = spec.startMeters
        private var heading = 1.0

        fun advance(nowMillis: Long, random: Random): BleAdvertisement {
            meters += heading * spec.metersPerTick
            if (meters <= spec.minMeters || meters >= spec.maxMeters) {
                heading = -heading
                meters = meters.coerceIn(spec.minMeters, spec.maxMeters)
            }
            return BleAdvertisement(
                address = spec.address,
                name = spec.name,
                rssi = rssiFor(meters, random),
                txPower = spec.txPower,
                isConnectable = spec.isConnectable,
                timestampMillis = nowMillis,
            )
        }

        private fun rssiFor(meters: Double, random: Random): Int {
            val calibration = RssiCalibration.Default
            val pathLoss = PATH_LOSS_FACTOR * calibration.pathLossExponent * log10(meters)
            val noise = (random.nextDouble() - CENTER) * 2 * spec.noiseDb
            return (calibration.referenceRssiAtOneMeter - pathLoss + noise).roundToInt()
        }

        private companion object {
            const val PATH_LOSS_FACTOR = 10.0
            const val CENTER = 0.5
        }
    }
}

object SimulatedBleEnvironment : BleEnvironment {
    override fun readiness(): BleReadiness = BleReadiness.Ready
    override fun readinessChanges(): Flow<BleReadiness> = emptyFlow()
    override fun refresh() = Unit
}
