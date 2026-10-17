package id.renaldi.proximity.core.model

data class DeviceFilter(
    val query: String = "",
    val minRssi: Int = NO_THRESHOLD,
    val onlyNamed: Boolean = false,
    val sort: DeviceSort = DeviceSort.STRONGEST_SIGNAL,
) {
    val isActive: Boolean
        get() = query.isNotBlank() || minRssi != NO_THRESHOLD || onlyNamed ||
            sort != DeviceSort.STRONGEST_SIGNAL

    val activeCount: Int
        get() = listOf(
            query.isNotBlank(),
            minRssi != NO_THRESHOLD,
            onlyNamed,
            sort != DeviceSort.STRONGEST_SIGNAL,
        ).count { it }

    fun matches(device: BleDevice): Boolean {
        if (device.smoothedRssi < minRssi) return false
        if (onlyNamed && !device.hasName) return false
        if (query.isBlank()) return true
        val needle = query.trim()
        return device.address.contains(needle, ignoreCase = true) ||
            device.name?.contains(needle, ignoreCase = true) == true
    }

    companion object {
        const val NO_THRESHOLD: Int = -100

        val RSSI_THRESHOLD_RANGE: IntRange = -100..-40

        val Default = DeviceFilter()
    }
}
