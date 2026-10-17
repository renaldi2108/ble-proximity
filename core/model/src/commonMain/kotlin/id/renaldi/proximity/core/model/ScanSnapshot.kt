package id.renaldi.proximity.core.model

data class DeviceFocus(
    val address: String,
    val stability: SignalStability,
)

data class ScanSnapshot(
    val devices: List<BleDevice>,
    val capturedAtMillis: Long,
    val focus: DeviceFocus? = null,
) {
    companion object {
        val Empty = ScanSnapshot(devices = emptyList(), capturedAtMillis = 0L)
    }
}
