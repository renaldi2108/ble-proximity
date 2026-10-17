package id.renaldi.proximity.core.model

data class BleAdvertisement(
    val address: String,
    val name: String?,
    val rssi: Int,
    val txPower: Int?,
    val isConnectable: Boolean,
    val timestampMillis: Long,
)
