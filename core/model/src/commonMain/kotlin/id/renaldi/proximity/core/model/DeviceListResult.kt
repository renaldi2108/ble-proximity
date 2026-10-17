package id.renaldi.proximity.core.model

data class DeviceListResult(
    val devices: List<BleDevice>,
    val visibleCount: Int,
    val totalCount: Int,
    val capturedAtMillis: Long,
) {
    val hiddenCount: Int get() = (totalCount - visibleCount).coerceAtLeast(0)

    val isEmpty: Boolean get() = devices.isEmpty()

    companion object {
        val Empty = DeviceListResult(
            devices = emptyList(),
            visibleCount = 0,
            totalCount = 0,
            capturedAtMillis = 0L,
        )
    }
}
