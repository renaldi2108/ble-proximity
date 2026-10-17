package id.renaldi.proximity.core.common

import id.renaldi.proximity.core.common.format.toFixed
import kotlin.test.Test
import kotlin.test.assertEquals

class NumberFormatTest {
    @Test
    fun `rounds half up at the requested precision`() {
        assertEquals("1,5", 1.45.toFixed(1))
        assertEquals("2", 1.5.toFixed(0))
    }

    @Test
    fun `pads missing fraction digits`() {
        assertEquals("3,00", 3.0.toFixed(2))
        assertEquals("3,10", 3.1.toFixed(2))
    }

    @Test
    fun `keeps the sign`() {
        assertEquals("-2,5", (-2.5).toFixed(1))
    }

    @Test
    fun `non finite values degrade gracefully`() {
        assertEquals("-", Double.NaN.toFixed(1))
        assertEquals("-", Double.POSITIVE_INFINITY.toFixed(1))
    }
}
