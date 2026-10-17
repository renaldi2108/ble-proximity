package id.renaldi.proximity.core.model

import kotlin.test.Test
import kotlin.test.assertEquals

class SignalStabilityTest {
    @Test
    fun `steady stream grades stable`() {
        val stability = SignalStability(
            packetsPerSecond = 5.0,
            jitterDb = 2.0,
            millisSinceLastPacket = 200L,
        )
        assertEquals(SignalStability.Grade.STABLE, stability.grade)
    }

    @Test
    fun `high jitter grades unstable`() {
        val stability = SignalStability(
            packetsPerSecond = 5.0,
            jitterDb = 9.0,
            millisSinceLastPacket = 200L,
        )
        assertEquals(SignalStability.Grade.UNSTABLE, stability.grade)
    }

    @Test
    fun `sparse packets grade unstable`() {
        val stability = SignalStability(
            packetsPerSecond = 0.4,
            jitterDb = 1.0,
            millisSinceLastPacket = 200L,
        )
        assertEquals(SignalStability.Grade.UNSTABLE, stability.grade)
    }

    @Test
    fun `silence past the stale window grades dropped`() {
        val stability = SignalStability(
            packetsPerSecond = 5.0,
            jitterDb = 1.0,
            millisSinceLastPacket = SignalStability.STALE_MILLIS + 1,
        )
        assertEquals(SignalStability.Grade.DROPPED, stability.grade)
    }
}
