package id.renaldi.proximity.core.model

data class DeviceSighting(
    val address: String,
    val name: String?,
    val strongestRssi: Int,
    val lastRssi: Int,
    val firstSeenAtMillis: Long,
    val lastSeenAtMillis: Long,
    val packetDelta: Long,
)
