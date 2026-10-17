package id.renaldi.proximity.feature.history.data

import app.cash.turbine.test
import id.renaldi.proximity.core.database.DatabaseDriverFactory
import id.renaldi.proximity.core.database.DatabaseFactory
import id.renaldi.proximity.core.model.DeviceSighting
import id.renaldi.proximity.core.testing.TestDispatcherProvider
import id.renaldi.proximity.core.testing.TestDevices
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest

class DeviceHistoryRepositoryImplTest {
    private val driver = DatabaseDriverFactory().create()
    private val database = DatabaseFactory.create(driver)
    private val repository = DeviceHistoryRepositoryImpl(
        database = database,
        dispatchers = TestDispatcherProvider(Dispatchers.Unconfined),
    )

    @AfterTest
    fun close() = driver.close()

    @Test
    fun `history starts empty`() = runTest {
        repository.history().test {
            assertTrue(awaitItem().isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `a sighting becomes a history entry`() = runTest {
        repository.record(listOf(sighting(rssi = -55, packets = 12)))

        repository.history().test {
            val entry = awaitItem().single()
            assertEquals(TestDevices.SPEAKER, entry.address)
            assertEquals("JBL Flip 6", entry.name)
            assertEquals(-55, entry.strongestRssi)
            assertEquals(12L, entry.sightingCount)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `repeat sightings accumulate instead of overwriting`() = runTest {
        repository.record(listOf(sighting(rssi = -70, packets = 10, firstSeen = 1_000, lastSeen = 2_000)))
        repository.record(listOf(sighting(rssi = -50, packets = 5, firstSeen = 3_000, lastSeen = 4_000)))

        repository.history().test {
            val entry = awaitItem().single()
            assertEquals(15L, entry.sightingCount)
            assertEquals(-50, entry.strongestRssi)
            assertEquals(-50, entry.lastRssi)
            assertEquals(1_000L, entry.firstSeenAtMillis)
            assertEquals(4_000L, entry.lastSeenAtMillis)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `a later nameless sighting does not erase a known name`() = runTest {
        repository.record(listOf(sighting(rssi = -55, packets = 1, name = "JBL Flip 6")))
        repository.record(listOf(sighting(rssi = -55, packets = 1, name = null)))

        repository.history().test {
            assertEquals("JBL Flip 6", awaitItem().single().name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `history is ordered by last seen descending`() = runTest {
        repository.record(
            listOf(
                sighting(address = TestDevices.SPEAKER, rssi = -55, packets = 1, lastSeen = 2_000),
                sighting(address = TestDevices.BUDS, rssi = -45, packets = 1, lastSeen = 9_000),
            ),
        )

        repository.history().test {
            assertEquals(
                listOf(TestDevices.BUDS, TestDevices.SPEAKER),
                awaitItem().map { it.address },
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `storage growth is capped by the row limit`() = runTest {
        val batch = (1..(DatabaseFactory.HISTORY_ROW_LIMIT.toInt() + 40)).map { index ->
            sighting(
                address = "AA:BB:CC:DD:${index / 100}:${index % 100}",
                rssi = -60,
                packets = 1,
                lastSeen = 1_000L + index,
            )
        }
        repository.record(batch)

        assertEquals(
            DatabaseFactory.HISTORY_ROW_LIMIT,
            database.deviceHistoryQueries.countAll().executeAsOne(),
        )
    }

    @Test
    fun `empty batch is a no-op`() = runTest {
        repository.record(emptyList())

        assertEquals(0L, database.deviceHistoryQueries.countAll().executeAsOne())
    }

    @Test
    fun `single entry can be deleted`() = runTest {
        repository.record(listOf(sighting(rssi = -55, packets = 1)))
        repository.delete(TestDevices.SPEAKER)

        repository.entry(TestDevices.SPEAKER).test {
            assertNull(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clear empties the table`() = runTest {
        repository.record(
            listOf(
                sighting(address = TestDevices.SPEAKER, rssi = -55, packets = 1),
                sighting(address = TestDevices.BUDS, rssi = -45, packets = 1),
            ),
        )
        repository.clear()

        assertEquals(0L, database.deviceHistoryQueries.countAll().executeAsOne())
    }

    @Test
    fun `history reacts to writes while being observed`() = runTest {
        repository.history().test {
            assertTrue(awaitItem().isEmpty())

            repository.record(listOf(sighting(rssi = -55, packets = 1)))

            assertEquals(1, awaitItem().size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun sighting(
        address: String = TestDevices.SPEAKER,
        rssi: Int,
        packets: Long,
        name: String? = "JBL Flip 6",
        firstSeen: Long = 1_000L,
        lastSeen: Long = 2_000L,
    ) = DeviceSighting(
        address = address,
        name = name,
        strongestRssi = rssi,
        lastRssi = rssi,
        firstSeenAtMillis = firstSeen,
        lastSeenAtMillis = lastSeen,
        packetDelta = packets,
    )
}
