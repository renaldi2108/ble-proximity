package id.renaldi.proximity.core.model

data class DeviceHistoryEntry(
    val address: String,
    val name: String?,
    val strongestRssi: Int,
    val lastRssi: Int,
    val firstSeenAtMillis: Long,
    val lastSeenAtMillis: Long,
    val sightingCount: Long,
) {
    val displayName: String get() = name ?: BleDevice.UNNAMED

    val closestZone: ProximityZone get() = ProximityZone.of(strongestRssi)

    val lastZone: ProximityZone get() = ProximityZone.of(lastRssi)
}
