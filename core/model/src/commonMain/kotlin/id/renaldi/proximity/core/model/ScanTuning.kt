package id.renaldi.proximity.core.model

enum class ScanPower { LOW_POWER, BALANCED, LOW_LATENCY }

data class ScanTuning(
    val power: ScanPower = ScanPower.BALANCED,
    val emitIntervalMillis: Long = 250L,
    val lostAfterMillis: Long = 3_000L,
    val staleAfterMillis: Long = 30_000L,
    val rssiWindow: Int = 7,
    val maxDevices: Int = 256,
    val persistIntervalMillis: Long = 5_000L,
    val traceCapacity: Int = 48,
) {
    companion object {
        val Default = ScanTuning()

        val Thermal = ScanTuning(
            power = ScanPower.LOW_POWER,
            emitIntervalMillis = 1_000L,
            traceCapacity = 24,
        )
    }
}
