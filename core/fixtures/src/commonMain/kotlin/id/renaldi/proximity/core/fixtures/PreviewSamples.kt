package id.renaldi.proximity.core.fixtures

import id.renaldi.proximity.core.model.BleDevice
import id.renaldi.proximity.core.model.DeviceHistoryEntry
import id.renaldi.proximity.core.model.DeviceListResult
import id.renaldi.proximity.core.model.ProximityZone
import id.renaldi.proximity.core.model.SignalStability
import id.renaldi.proximity.core.model.SignalTrend
import id.renaldi.proximity.core.model.TrackedDevice

object PreviewSamples {
    const val NOW_MILLIS: Long = 1_760_000_000_000L

    val buds = device(
        address = "C4:1A:9E:72:00:11",
        name = "Galaxy Buds Pro",
        rssi = -41,
        txPower = -7,
    )

    val mouse = device(
        address = "1D:48:B7:66:0A:F4",
        name = "Logitech MX Master",
        rssi = -28,
        txPower = -12,
    )

    val speaker = device(
        address = "A0:6F:32:1C:48:9D",
        name = "JBL Flip 6",
        rssi = -58,
    )

    val band = device(
        address = "F8:4D:89:2B:71:03",
        name = "Mi Band 8",
        rssi = -74,
        isConnectable = false,
    )

    val anonymous = device(
        address = "3E:11:C0:AA:55:7F",
        name = null,
        rssi = -83,
        isConnectable = false,
    )

    val faraway = device(
        address = "7B:90:DE:04:C2:68",
        name = "Tile Mate",
        rssi = -88,
    )

    val lost = device(
        address = "92:AC:5D:38:E1:20",
        name = "ESP32-Beacon",
        rssi = BleDevice.LOST_RSSI,
        secondsAgo = 12,
    )

    val devices: List<BleDevice> = listOf(mouse, buds, speaker, band, anonymous, faraway, lost)

    val deviceList = DeviceListResult(
        devices = devices,
        visibleCount = devices.size,
        totalCount = devices.size,
        capturedAtMillis = NOW_MILLIS,
    )

    val filteredDeviceList = DeviceListResult(
        devices = listOf(mouse, buds, speaker),
        visibleCount = 3,
        totalCount = devices.size,
        capturedAtMillis = NOW_MILLIS,
    )

    val rssiTrace: List<Int> =
        listOf(-72, -70, -71, -68, -66, -67, -63, -61, -62, -59, -57, -58, -55, -54, -52, -51)

    val trackedNear = tracked(device = buds, trend = SignalTrend.APPROACHING)

    val trackedGood = tracked(device = speaker, trend = SignalTrend.STABLE)

    val trackedWeak = tracked(device = band, trend = SignalTrend.RECEDING)

    val trackedLost = TrackedDevice(
        device = lost,
        trend = SignalTrend.LOST,
        stability = SignalStability(
            packetsPerSecond = 0.0,
            jitterDb = 0.0,
            millisSinceLastPacket = 12_000L,
        ),
        rssiHistory = listOf(-78, -82, -86, -90, BleDevice.LOST_RSSI, BleDevice.LOST_RSSI),
    )

    val historyEntries: List<DeviceHistoryEntry> = listOf(
        historyEntry(buds, strongestRssi = -36, lastRssi = -41, sightings = 1_284, secondsAgo = 8),
        historyEntry(speaker, strongestRssi = -47, lastRssi = -58, sightings = 642, secondsAgo = 95),
        historyEntry(band, strongestRssi = -66, lastRssi = -74, sightings = 311, secondsAgo = 2_400),
        historyEntry(anonymous, strongestRssi = -79, lastRssi = -83, sightings = 48, secondsAgo = 86_400),
    )

    fun deviceInZone(zone: ProximityZone): BleDevice = device(
        address = "AA:BB:CC:DD:EE:0${zone.ordinal}",
        name = "Perangkat ${zone.ordinal + 1}",
        rssi = if (zone == ProximityZone.LOST) BleDevice.LOST_RSSI else zone.minRssi,
    )

    fun tracked(
        device: BleDevice,
        trend: SignalTrend = SignalTrend.STABLE,
        grade: SignalStability.Grade = SignalStability.Grade.STABLE,
    ) = TrackedDevice(
        device = device,
        trend = trend,
        stability = when (grade) {
            SignalStability.Grade.STABLE ->
                SignalStability(packetsPerSecond = 8.4, jitterDb = 1.6, millisSinceLastPacket = 120)

            SignalStability.Grade.UNSTABLE ->
                SignalStability(packetsPerSecond = 2.1, jitterDb = 8.3, millisSinceLastPacket = 480)

            SignalStability.Grade.DROPPED ->
                SignalStability(packetsPerSecond = 0.0, jitterDb = 0.0, millisSinceLastPacket = 9_000)
        },
        rssiHistory = rssiTrace,
    )

    private fun device(
        address: String,
        name: String?,
        rssi: Int,
        txPower: Int? = null,
        isConnectable: Boolean = true,
        secondsAgo: Long = 0,
    ) = BleDevice(
        address = address,
        name = name,
        rawRssi = rssi,
        smoothedRssi = rssi,
        lastSeenAtMillis = NOW_MILLIS - secondsAgo * 1_000,
        firstSeenAtMillis = NOW_MILLIS - 180_000,
        packetCount = 734,
        isConnectable = isConnectable,
        txPower = txPower,
    )

    private fun historyEntry(
        device: BleDevice,
        strongestRssi: Int,
        lastRssi: Int,
        sightings: Long,
        secondsAgo: Long,
    ) = DeviceHistoryEntry(
        address = device.address,
        name = device.name,
        strongestRssi = strongestRssi,
        lastRssi = lastRssi,
        firstSeenAtMillis = NOW_MILLIS - 7 * 86_400_000L,
        lastSeenAtMillis = NOW_MILLIS - secondsAgo * 1_000,
        sightingCount = sightings,
    )
}
