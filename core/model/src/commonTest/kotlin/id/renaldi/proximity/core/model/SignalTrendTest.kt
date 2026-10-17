package id.renaldi.proximity.core.model

import kotlin.test.Test
import kotlin.test.assertEquals

class SignalTrendTest {
    @Test
    fun `rising signal means approaching`() {
        assertEquals(
            SignalTrend.APPROACHING,
            SignalTrend.of(oldestRssi = -70, latestRssi = -60, isLost = false),
        )
    }

    @Test
    fun `falling signal means receding`() {
        assertEquals(
            SignalTrend.RECEDING,
            SignalTrend.of(oldestRssi = -60, latestRssi = -70, isLost = false),
        )
    }

    @Test
    fun `noise below threshold stays stable`() {
        assertEquals(
            SignalTrend.STABLE,
            SignalTrend.of(oldestRssi = -65, latestRssi = -62, isLost = false),
        )
    }

    @Test
    fun `lost signal overrides direction`() {
        assertEquals(
            SignalTrend.LOST,
            SignalTrend.of(oldestRssi = -95, latestRssi = -60, isLost = true),
        )
    }
}
