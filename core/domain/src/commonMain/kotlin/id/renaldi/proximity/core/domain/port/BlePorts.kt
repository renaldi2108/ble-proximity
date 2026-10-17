package id.renaldi.proximity.core.domain.port

import id.renaldi.proximity.core.model.BleAdvertisement
import id.renaldi.proximity.core.model.BleFailure
import id.renaldi.proximity.core.model.BleReadiness
import id.renaldi.proximity.core.model.ScanTuning
import kotlinx.coroutines.flow.Flow

class BleScanException(val failure: BleFailure) : Exception(failure.toString())

interface BleScanner {
    fun advertisements(tuning: ScanTuning): Flow<BleAdvertisement>
}

interface BleEnvironment {
    fun readiness(): BleReadiness

    fun readinessChanges(): Flow<BleReadiness>

    fun refresh()
}
