package id.renaldi.proximity.core.model

data class TrackedDevice(
    val device: BleDevice,
    val trend: SignalTrend,
    val stability: SignalStability,
    val rssiHistory: List<Int>,
) {
    val isLost: Boolean get() = device.proximity.zone == ProximityZone.LOST

    val strongestRssi: Int get() = rssiHistory.maxOrNull() ?: device.smoothedRssi

    val weakestRssi: Int get() = rssiHistory.minOrNull() ?: device.smoothedRssi
}
