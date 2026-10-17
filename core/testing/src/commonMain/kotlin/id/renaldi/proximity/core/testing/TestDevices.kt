package id.renaldi.proximity.core.testing

import id.renaldi.proximity.core.model.BleDevice
import id.renaldi.proximity.core.model.DeviceHistoryEntry

object TestDevices {
    const val SPEAKER: String = "A0:6F:32:1C:48:9D"
    const val BUDS: String = "C4:1A:9E:72:00:11"
    const val ANONYMOUS: String = "3E:11:C0:AA:55:7F"

    fun device(
        address: String = SPEAKER,
        name: String? = "JBL Flip 6",
        rssi: Int = -55,
        lastSeenAtMillis: Long = 1_000L,
        firstSeenAtMillis: Long = 1_000L,
        packetCount: Int = 10,
        isConnectable: Boolean = true,
    ) = BleDevice(
        address = address,
        name = name,
        rawRssi = rssi,
        smoothedRssi = rssi,
        lastSeenAtMillis = lastSeenAtMillis,
        firstSeenAtMillis = firstSeenAtMillis,
        packetCount = packetCount,
        isConnectable = isConnectable,
    )

    fun historyEntry(
        address: String = SPEAKER,
        name: String? = "JBL Flip 6",
        strongestRssi: Int = -48,
        lastRssi: Int = -62,
        firstSeenAtMillis: Long = 1_000L,
        lastSeenAtMillis: Long = 9_000L,
        sightingCount: Long = 120L,
    ) = DeviceHistoryEntry(
        address = address,
        name = name,
        strongestRssi = strongestRssi,
        lastRssi = lastRssi,
        firstSeenAtMillis = firstSeenAtMillis,
        lastSeenAtMillis = lastSeenAtMillis,
        sightingCount = sightingCount,
    )
}
