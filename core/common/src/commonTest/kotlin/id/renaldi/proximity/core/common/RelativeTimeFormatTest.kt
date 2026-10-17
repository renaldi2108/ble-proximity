package id.renaldi.proximity.core.common

import id.renaldi.proximity.core.common.format.RelativeTimeFormat
import kotlin.test.Test
import kotlin.test.assertEquals

class RelativeTimeFormatTest {
    private val now = 1_760_000_000_000L

    @Test
    fun `fresh timestamps read as just now`() {
        assertEquals("baru saja", RelativeTimeFormat.format(now - 3_000, now))
    }

    @Test
    fun `seconds minutes hours and days each have a bucket`() {
        assertEquals("30 detik lalu", RelativeTimeFormat.format(now - 30_000, now))
        assertEquals("5 menit lalu", RelativeTimeFormat.format(now - 300_000, now))
        assertEquals("3 jam lalu", RelativeTimeFormat.format(now - 10_800_000, now))
        assertEquals("2 hari lalu", RelativeTimeFormat.format(now - 172_800_000, now))
    }

    @Test
    fun `clock skew never produces negative text`() {
        assertEquals("baru saja", RelativeTimeFormat.format(now + 60_000, now))
    }

    @Test
    fun `beyond a week falls back to an absolute date`() {
        val formatted = RelativeTimeFormat.format(now - 30L * 24 * 3_600_000, now)
        assertEquals(RelativeTimeFormat.formatAbsolute(now - 30L * 24 * 3_600_000), formatted)
    }

    @Test
    fun `duration text is compact`() {
        assertEquals("45s", RelativeTimeFormat.formatDuration(45_000))
        assertEquals("2m 5s", RelativeTimeFormat.formatDuration(125_000))
        assertEquals("1j 1m", RelativeTimeFormat.formatDuration(3_660_000))
    }
}
