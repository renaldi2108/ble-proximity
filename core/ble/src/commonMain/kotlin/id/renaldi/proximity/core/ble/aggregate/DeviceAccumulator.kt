package id.renaldi.proximity.core.ble.aggregate

import id.renaldi.proximity.core.model.BleAdvertisement
import id.renaldi.proximity.core.model.BleDevice
import id.renaldi.proximity.core.model.DeviceSighting
import id.renaldi.proximity.core.model.SignalStability
import kotlin.math.abs

internal class DeviceAccumulator(val address: String, windowSize: Int) {
    private val window = IntArray(windowSize) { SENTINEL }
    private val scratch = IntArray(windowSize)
    private val stamps = LongArray(windowSize)
    private var filled = 0
    private var cursor = 0

    private var name: String? = null
    private var rawRssi = BleDevice.LOST_RSSI
    private var txPower: Int? = null
    private var connectable = false
    private var firstSeenMillis = 0L
    private var packetCount = 0L
    private var strongestRssi = BleDevice.LOST_RSSI

    private var changedSinceSnapshot = true
    private var packetsSincePersist = 0L
    private var cachedDevice: BleDevice? = null

    var lastSeenMillis = 0L
        private set

    val hasUnpersistedPackets: Boolean get() = packetsSincePersist > 0

    fun accept(advertisement: BleAdvertisement) {
        if (packetCount == 0L) firstSeenMillis = advertisement.timestampMillis
        advertisement.name?.let { name = it }
        advertisement.txPower?.let { txPower = it }
        connectable = advertisement.isConnectable
        rawRssi = advertisement.rssi
        lastSeenMillis = advertisement.timestampMillis
        packetCount++
        packetsSincePersist++
        if (advertisement.rssi > strongestRssi) strongestRssi = advertisement.rssi

        window[cursor] = advertisement.rssi
        stamps[cursor] = advertisement.timestampMillis
        cursor = (cursor + 1) % window.size
        if (filled < window.size) filled++
        changedSinceSnapshot = true
    }

    fun toDevice(nowMillis: Long, lostAfterMillis: Long): BleDevice {
        val smoothed = smoothedRssi(nowMillis, lostAfterMillis)
        val cached = cachedDevice
        if (!changedSinceSnapshot && cached != null && cached.smoothedRssi == smoothed) return cached

        val device = BleDevice(
            address = address,
            name = name,
            rawRssi = rawRssi,
            smoothedRssi = smoothed,
            lastSeenAtMillis = lastSeenMillis,
            firstSeenAtMillis = firstSeenMillis,
            packetCount = packetCount.toInt(),
            isConnectable = connectable,
            txPower = txPower,
        )
        cachedDevice = device
        changedSinceSnapshot = false
        return device
    }

    fun stability(nowMillis: Long): SignalStability = SignalStability(
        packetsPerSecond = packetsPerSecond(),
        jitterDb = jitterDb(),
        millisSinceLastPacket = (nowMillis - lastSeenMillis).coerceAtLeast(0L),
    )

    fun drainSighting(): DeviceSighting? {
        if (packetsSincePersist == 0L) return null
        val sighting = DeviceSighting(
            address = address,
            name = name,
            strongestRssi = strongestRssi,
            lastRssi = rawRssi,
            firstSeenAtMillis = firstSeenMillis,
            lastSeenAtMillis = lastSeenMillis,
            packetDelta = packetsSincePersist,
        )
        packetsSincePersist = 0L
        return sighting
    }

    private fun smoothedRssi(nowMillis: Long, lostAfterMillis: Long): Int =
        if (nowMillis - lastSeenMillis > lostAfterMillis) BleDevice.LOST_RSSI else median()

    private fun median(): Int {
        if (filled == 0) return BleDevice.LOST_RSSI
        window.copyInto(scratch)
        scratch.sort()
        return scratch[window.size - filled + filled / 2]
    }

    private fun jitterDb(): Double {
        if (filled < 2) return 0.0
        val center = median()
        var total = 0.0
        window.forEach { value -> if (value != SENTINEL) total += abs(value - center).toDouble() }
        return total / filled
    }

    private fun packetsPerSecond(): Double {
        if (filled < 2) return 0.0
        val oldestIndex = if (filled < window.size) 0 else cursor
        val span = (lastSeenMillis - stamps[oldestIndex]).coerceAtLeast(1L)
        return (filled - 1) * MILLIS_PER_SECOND / span
    }

    private companion object {
        const val SENTINEL = Int.MIN_VALUE
        const val MILLIS_PER_SECOND = 1_000.0
    }
}
