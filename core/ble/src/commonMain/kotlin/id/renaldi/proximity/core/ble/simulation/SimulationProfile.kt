package id.renaldi.proximity.core.ble.simulation

data class SimulatedDevice(
    val address: String,
    val name: String?,
    val startMeters: Double,
    val minMeters: Double,
    val maxMeters: Double,
    val metersPerTick: Double,
    val noiseDb: Double,
    val isConnectable: Boolean = true,
    val txPower: Int? = null,
)

data class SimulationProfile(
    val devices: List<SimulatedDevice>,
    val packetIntervalMillis: Long = 60L,
    val seed: Int = 20_261_007,
) {
    companion object {
        val Default = SimulationProfile(
            devices = listOf(
                SimulatedDevice(
                    address = "C4:1A:9E:72:00:11",
                    name = "Galaxy Buds Pro",
                    startMeters = 0.6,
                    minMeters = 0.3,
                    maxMeters = 2.5,
                    metersPerTick = 0.04,
                    noiseDb = 2.0,
                    txPower = -7,
                ),
                SimulatedDevice(
                    address = "A0:6F:32:1C:48:9D",
                    name = "JBL Flip 6",
                    startMeters = 2.4,
                    minMeters = 1.0,
                    maxMeters = 8.0,
                    metersPerTick = 0.12,
                    noiseDb = 3.0,
                ),
                SimulatedDevice(
                    address = "F8:4D:89:2B:71:03",
                    name = "Mi Band 8",
                    startMeters = 5.0,
                    minMeters = 3.0,
                    maxMeters = 12.0,
                    metersPerTick = 0.08,
                    noiseDb = 4.0,
                    isConnectable = false,
                ),
                SimulatedDevice(
                    address = "3E:11:C0:AA:55:7F",
                    name = null,
                    startMeters = 9.0,
                    minMeters = 6.0,
                    maxMeters = 18.0,
                    metersPerTick = 0.3,
                    noiseDb = 5.0,
                    isConnectable = false,
                ),
                SimulatedDevice(
                    address = "7B:90:DE:04:C2:68",
                    name = "Tile Mate",
                    startMeters = 14.0,
                    minMeters = 10.0,
                    maxMeters = 26.0,
                    metersPerTick = 0.45,
                    noiseDb = 6.0,
                ),
                SimulatedDevice(
                    address = "92:AC:5D:38:E1:20",
                    name = null,
                    startMeters = 22.0,
                    minMeters = 15.0,
                    maxMeters = 60.0,
                    metersPerTick = 1.2,
                    noiseDb = 7.0,
                    isConnectable = false,
                ),
                SimulatedDevice(
                    address = "1D:48:B7:66:0A:F4",
                    name = "Logitech MX Master",
                    startMeters = 1.2,
                    minMeters = 0.8,
                    maxMeters = 4.0,
                    metersPerTick = 0.05,
                    noiseDb = 2.5,
                    txPower = -12,
                ),
                SimulatedDevice(
                    address = "55:02:9F:D1:3C:87",
                    name = "ESP32-Beacon",
                    startMeters = 3.5,
                    minMeters = 2.0,
                    maxMeters = 9.0,
                    metersPerTick = 0.15,
                    noiseDb = 3.5,
                    isConnectable = false,
                    txPower = 0,
                ),
            ),
        )
    }
}
