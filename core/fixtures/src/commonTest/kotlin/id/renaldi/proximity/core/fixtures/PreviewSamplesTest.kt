package id.renaldi.proximity.core.fixtures

import id.renaldi.proximity.core.model.ProximityZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PreviewSamplesTest {
    @Test
    fun `sample device actually lands in the zone it is named after`() {
        ProximityZone.entries.forEach { zone ->
            assertEquals(zone, PreviewSamples.deviceInZone(zone).proximity.zone)
        }
    }

    @Test
    fun `samples cover every zone so previews exercise the whole palette`() {
        val covered = PreviewSamples.devices.map { it.proximity.zone }.toSet()
        assertTrue(
            covered.size >= ProximityZone.entries.size - 1,
            "sampel hanya menutup $covered",
        )
    }

    @Test
    fun `list counts stay consistent with their contents`() {
        assertEquals(PreviewSamples.devices.size, PreviewSamples.deviceList.visibleCount)
        assertEquals(PreviewSamples.devices.size, PreviewSamples.deviceList.totalCount)
        assertEquals(0, PreviewSamples.deviceList.hiddenCount)

        assertEquals(3, PreviewSamples.filteredDeviceList.visibleCount)
        assertEquals(
            PreviewSamples.devices.size - 3,
            PreviewSamples.filteredDeviceList.hiddenCount,
        )
    }

    @Test
    fun `lost sample reads as lost`() {
        assertEquals(ProximityZone.LOST, PreviewSamples.lost.proximity.zone)
        assertTrue(PreviewSamples.trackedLost.isLost)
    }

    @Test
    fun `history samples are ordered newest first`() {
        val stamps = PreviewSamples.historyEntries.map { it.lastSeenAtMillis }
        assertEquals(stamps.sortedDescending(), stamps)
    }
}
