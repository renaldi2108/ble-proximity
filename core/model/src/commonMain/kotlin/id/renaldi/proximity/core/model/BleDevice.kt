package id.renaldi.proximity.core.model

data class BleDevice(
    val address: String,
    val name: String?,
    val rawRssi: Int,
    val smoothedRssi: Int,
    val lastSeenAtMillis: Long,
    val firstSeenAtMillis: Long,
    val packetCount: Int,
    val isConnectable: Boolean,
    val txPower: Int? = null,
) {
    val proximity: Proximity = Proximity.of(smoothedRssi)

    val displayName: String get() = name ?: UNNAMED

    val hasName: Boolean get() = name != null

    fun ageMillis(nowMillis: Long): Long = (nowMillis - lastSeenAtMillis).coerceAtLeast(0L)

    fun asLost(): BleDevice = if (smoothedRssi == LOST_RSSI) this else copy(smoothedRssi = LOST_RSSI)

    companion object {
        const val UNNAMED: String = "Tanpa nama"

        const val LOST_RSSI: Int = -127
    }
}
