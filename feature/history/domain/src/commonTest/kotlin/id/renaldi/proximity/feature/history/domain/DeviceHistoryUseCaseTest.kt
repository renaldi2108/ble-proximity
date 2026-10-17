package id.renaldi.proximity.feature.history.domain

import app.cash.turbine.test
import id.renaldi.proximity.core.common.AppResult
import id.renaldi.proximity.core.testing.FakeDeviceHistoryRepository
import id.renaldi.proximity.core.testing.TestDevices
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class DeviceHistoryUseCaseTest {
    private val repository = FakeDeviceHistoryRepository(
        initial = listOf(
            TestDevices.historyEntry(TestDevices.SPEAKER, lastSeenAtMillis = 5_000),
            TestDevices.historyEntry(TestDevices.BUDS, name = "Galaxy Buds Pro", lastSeenAtMillis = 9_000),
        ),
    )

    @Test
    fun `history is newest first`() = runTest {
        ObserveDeviceHistory(repository)().test {
            assertEquals(
                listOf(TestDevices.BUDS, TestDevices.SPEAKER),
                awaitItem().map { it.address },
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `delete removes a single entry`() = runTest {
        val result = DeleteHistoryEntry(repository)(TestDevices.SPEAKER)

        assertTrue(result.isSuccess)
        ObserveDeviceHistory(repository)().test {
            assertEquals(listOf(TestDevices.BUDS), awaitItem().map { it.address })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clear empties the log`() = runTest {
        assertTrue(ClearDeviceHistory(repository)().isSuccess)

        ObserveDeviceHistory(repository)().test {
            assertTrue(awaitItem().isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `storage failure is surfaced instead of crashing`() = runTest {
        repository.failOnWrite = true

        val result = DeleteHistoryEntry(repository)(TestDevices.SPEAKER)

        val failure = assertIs<AppResult.Failure>(result)
        assertEquals("Gagal menghapus riwayat perangkat", failure.message)
    }
}
