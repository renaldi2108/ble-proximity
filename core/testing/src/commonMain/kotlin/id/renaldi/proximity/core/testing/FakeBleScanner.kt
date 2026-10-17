package id.renaldi.proximity.core.testing

import id.renaldi.proximity.core.domain.port.BleScanException
import id.renaldi.proximity.core.domain.port.BleScanner
import id.renaldi.proximity.core.model.BleAdvertisement
import id.renaldi.proximity.core.model.BleFailure
import id.renaldi.proximity.core.model.ScanTuning
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

class FakeBleScanner(private val failWith: BleFailure? = null) : BleScanner {
    private val packets = MutableSharedFlow<BleAdvertisement>(
        extraBufferCapacity = 256,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    var startCount: Int = 0
        private set

    var lastTuning: ScanTuning? = null
        private set

    override fun advertisements(tuning: ScanTuning): Flow<BleAdvertisement> {
        if (failWith != null) return flow { throw BleScanException(failWith) }
        return packets.onStart {
            startCount++
            lastTuning = tuning
        }
    }

    suspend fun emit(advertisement: BleAdvertisement) = packets.emit(advertisement)

    suspend fun emit(
        address: String,
        rssi: Int,
        atMillis: Long,
        name: String? = null,
    ) = emit(
        BleAdvertisement(
            address = address,
            name = name,
            rssi = rssi,
            txPower = null,
            isConnectable = true,
            timestampMillis = atMillis,
        ),
    )
}
