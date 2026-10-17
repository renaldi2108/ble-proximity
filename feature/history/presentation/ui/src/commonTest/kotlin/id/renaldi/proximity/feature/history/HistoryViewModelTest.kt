package id.renaldi.proximity.feature.history

import app.cash.turbine.test
import id.renaldi.proximity.feature.history.domain.ClearDeviceHistory
import id.renaldi.proximity.feature.history.domain.DeleteHistoryEntry
import id.renaldi.proximity.feature.history.domain.ObserveDeviceHistory
import id.renaldi.proximity.core.testing.FakeDeviceHistoryRepository
import id.renaldi.proximity.core.testing.MutableAppClock
import id.renaldi.proximity.core.testing.TestDevices
import id.renaldi.proximity.core.testing.awaitUntil
import id.renaldi.proximity.core.testing.runViewModelTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class HistoryViewModelTest {
    private val clock = MutableAppClock(current = 10_000L)

    private fun viewModel(repository: FakeDeviceHistoryRepository) = HistoryViewModel(
        clock = clock,
        deleteEntry = DeleteHistoryEntry(repository),
        clearHistory = ClearDeviceHistory(repository),
        observeHistory = ObserveDeviceHistory(repository),
    )

    @Test
    fun `an empty database reports an empty log`() = runViewModelTest {
        viewModel(FakeDeviceHistoryRepository()).state.test {
            assertEquals(HistoryContent.Loading, awaitItem().content)
            val state = awaitUntil { it.content is HistoryContent.Empty }
            assertFalse(state.canClear)
            assertEquals(0, state.count)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `stored devices are listed newest first`() = runViewModelTest {
        val repository = FakeDeviceHistoryRepository(
            listOf(
                TestDevices.historyEntry(TestDevices.SPEAKER, lastSeenAtMillis = 2_000),
                TestDevices.historyEntry(TestDevices.BUDS, lastSeenAtMillis = 8_000),
            ),
        )

        viewModel(repository).state.test {
            awaitItem()
            val entries = assertIs<HistoryContent.Entries>(
                awaitUntil { it.content is HistoryContent.Entries }.content,
            )
            assertEquals(
                listOf(TestDevices.BUDS, TestDevices.SPEAKER),
                entries.entries.map { it.address },
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleting an entry removes it from the log`() = runViewModelTest {
        val repository = FakeDeviceHistoryRepository(
            listOf(
                TestDevices.historyEntry(TestDevices.SPEAKER),
                TestDevices.historyEntry(TestDevices.BUDS, lastSeenAtMillis = 8_000),
            ),
        )
        val model = viewModel(repository)

        model.state.test {
            awaitItem()
            awaitUntil { it.content is HistoryContent.Entries }

            model.onEvent(HistoryAction.Delete(TestDevices.SPEAKER))

            val entries = assertIs<HistoryContent.Entries>(
                awaitUntil {
                    (it.content as? HistoryContent.Entries)?.entries?.size == 1
                }.content,
            )
            assertEquals(listOf(TestDevices.BUDS), entries.entries.map { it.address })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clearing asks for confirmation first`() = runViewModelTest {
        val repository = FakeDeviceHistoryRepository(listOf(TestDevices.historyEntry()))
        val model = viewModel(repository)

        model.state.test {
            awaitItem()
            awaitUntil { it.content is HistoryContent.Entries }

            model.onEvent(HistoryAction.RequestClear)
            assertTrue(awaitItem().isClearDialogOpen)

            model.onEvent(HistoryAction.DismissClear)
            assertFalse(awaitItem().isClearDialogOpen)
            assertIs<HistoryContent.Entries>(model.state.value.content)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `confirming the dialog empties the log`() = runViewModelTest {
        val repository = FakeDeviceHistoryRepository(listOf(TestDevices.historyEntry()))
        val model = viewModel(repository)

        model.state.test {
            awaitItem()
            awaitUntil { it.content is HistoryContent.Entries }

            model.onEvent(HistoryAction.RequestClear)
            awaitUntil { it.isClearDialogOpen }
            model.onEvent(HistoryAction.ConfirmClear)

            val state = awaitUntil { it.content is HistoryContent.Empty }
            assertFalse(state.isClearDialogOpen)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `a storage failure is reported instead of crashing the screen`() = runViewModelTest {
        val repository = FakeDeviceHistoryRepository(listOf(TestDevices.historyEntry()))
        val model = viewModel(repository)

        model.state.test {
            awaitItem()
            awaitUntil { it.content is HistoryContent.Entries }
            repository.failOnWrite = true

            model.effects.test {
                model.onEvent(HistoryAction.Delete(TestDevices.SPEAKER))
                val effect = assertIs<HistoryEffect.ShowMessage>(awaitItem())
                assertEquals("Gagal menghapus riwayat perangkat", effect.message)
                cancelAndIgnoreRemainingEvents()
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `resuming the screen refreshes the relative timestamps`() = runViewModelTest {
        val model = viewModel(FakeDeviceHistoryRepository())

        model.state.test {
            assertEquals(10_000L, awaitItem().nowMillis)
            clock.set(90_000L)

            model.onEvent(HistoryAction.ScreenResumed)
            assertEquals(90_000L, awaitUntil { it.nowMillis == 90_000L }.nowMillis)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
