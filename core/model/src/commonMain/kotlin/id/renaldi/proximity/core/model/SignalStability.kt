package id.renaldi.proximity.core.model

data class SignalStability(
    val packetsPerSecond: Double,
    val jitterDb: Double,
    val millisSinceLastPacket: Long,
) {
    val grade: Grade
        get() = when {
            millisSinceLastPacket > STALE_MILLIS -> Grade.DROPPED
            jitterDb > UNSTABLE_JITTER_DB || packetsPerSecond < SPARSE_RATE -> Grade.UNSTABLE
            else -> Grade.STABLE
        }

    enum class Grade { STABLE, UNSTABLE, DROPPED }

    companion object {
        const val STALE_MILLIS: Long = 3_000L

        private const val UNSTABLE_JITTER_DB = 6.0
        private const val SPARSE_RATE = 1.0

        val Unknown = SignalStability(
            packetsPerSecond = 0.0,
            jitterDb = 0.0,
            millisSinceLastPacket = 0L,
        )
    }
}
