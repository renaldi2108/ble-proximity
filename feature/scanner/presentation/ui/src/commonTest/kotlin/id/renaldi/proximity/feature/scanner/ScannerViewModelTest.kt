package id.renaldi.proximity.feature.scanner

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import id.renaldi.proximity.core.domain.usecase.ObserveBleReadiness
import id.renaldi.proximity.feature.scanner.domain.ObserveVisibleDevices
import id.renaldi.proximity.core.model.BleFailure
import id.renaldi.proximity.core.model.BleReadiness
import id.renaldi.proximity.core.model.DeviceFilter
import id.renaldi.proximity.core.model.DeviceSort
import id.renaldi.proximity.core.model.ScanStatus
import id.renaldi.proximity.core.testing.FakeBleEnvironment
import id.renaldi.proximity.core.testing.FakeScanController
import id.renaldi.proximity.core.testing.TestDevices
import id.renaldi.proximity.core.testing.awaitUntil
import id.renaldi.proximity.core.testing.runViewModelTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ScannerViewModelTest {
    private val controller = FakeScanController()
    private val environment = FakeBleEnvironment()
    private val savedState = SavedStateHandle()

    private fun viewModel(handle: SavedStateHandle = savedState) = ScannerViewModel(
        controller = controller,
        savedState = handle,
        observeVisibleDevices = ObserveVisibleDevices(controller),
        observeReadiness = ObserveBleReadiness(environment),
    )

    @Test
    fun `opens idle and not scanning`() = runViewModelTest {
        viewModel().state.test {
            val state = awaitItem()
            assertEquals(ScannerContent.Idle, state.content)
            assertFalse(state.isScanning)
            assertNull(state.blocker)
            assertTrue(state.canScan)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggle starts then stops the scan`() = runViewModelTest {
        val model = viewModel()

        model.state.test {
            awaitItem()
            model.onEvent(ScannerAction.ToggleScan)
            assertTrue(awaitItem().isScanning)
            assertEquals(1, controller.startCount)

            model.onEvent(ScannerAction.ToggleScan)
            assertFalse(awaitItem().isScanning)
            assertEquals(1, controller.stopCount)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `devices from the scan become visible content sorted by signal`() = runViewModelTest {
        val model = viewModel()

        model.state.test {
            awaitItem()
            model.onEvent(ScannerAction.ToggleScan)
            awaitItem()

            controller.emit(
                listOf(
                    TestDevices.device(TestDevices.SPEAKER, "JBL Flip 6", rssi = -58),
                    TestDevices.device(TestDevices.BUDS, "Galaxy Buds Pro", rssi = -42),
                ),
            )

            val found = assertIs<ScannerContent.Found>(awaitItem().content)
            assertEquals(
                listOf(TestDevices.BUDS, TestDevices.SPEAKER),
                found.result.devices.map { it.address },
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `query that matches nothing reports a filtered empty list`() = runViewModelTest {
        val model = viewModel()

        model.state.test {
            awaitItem()
            controller.emit(listOf(TestDevices.device(TestDevices.SPEAKER, "JBL Flip 6")))
            awaitItem()

            model.onEvent(ScannerAction.QueryChanged("sony"))
            val state = awaitUntil { it.content is ScannerContent.NoMatch }

            assertEquals(ScannerContent.NoMatch(totalCount = 1), state.content)
            assertEquals(1, state.hiddenCount)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `threshold filter is applied to the live list`() = runViewModelTest {
        val model = viewModel()

        model.state.test {
            awaitItem()
            model.onEvent(ScannerAction.ThresholdChanged(-70))
            awaitItem()

            controller.emit(
                listOf(
                    TestDevices.device(TestDevices.SPEAKER, "JBL Flip 6", rssi = -58),
                    TestDevices.device(TestDevices.ANONYMOUS, null, rssi = -88),
                ),
            )

            val found = assertIs<ScannerContent.Found>(
                awaitUntil { it.content is ScannerContent.Found }.content,
            )
            assertEquals(listOf(TestDevices.SPEAKER), found.result.devices.map { it.address })
            assertEquals(1, found.result.hiddenCount)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `filter survives process death through saved state`() = runViewModelTest {
        val handle = SavedStateHandle()
        val first = viewModel(handle)

        first.state.test {
            awaitItem()
            first.onEvent(ScannerAction.QueryChanged("jbl"))
            first.onEvent(ScannerAction.ThresholdChanged(-70))
            first.onEvent(ScannerAction.OnlyNamedChanged(true))
            first.onEvent(ScannerAction.SortChanged(DeviceSort.NAME))
            awaitUntil { it.filter.sort == DeviceSort.NAME }
            cancelAndIgnoreRemainingEvents()
        }

        viewModel(handle).state.test {
            val restored = awaitItem().filter
            assertEquals("jbl", restored.query)
            assertEquals(-70, restored.minRssi)
            assertTrue(restored.onlyNamed)
            assertEquals(DeviceSort.NAME, restored.sort)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `reset returns the filter to its default`() = runViewModelTest {
        val model = viewModel()

        model.state.test {
            awaitItem()
            model.onEvent(ScannerAction.QueryChanged("jbl"))
            awaitItem()

            model.onEvent(ScannerAction.ResetFilter)
            assertEquals(DeviceFilter.Default, awaitUntil { !it.filter.isActive }.filter)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `blocked readiness disables scanning`() = runViewModelTest {
        val blocked = FakeBleEnvironment(BleReadiness.Blocked(BleFailure.PermissionDenied))
        val model = ScannerViewModel(
            controller = controller,
            savedState = savedState,
            observeVisibleDevices = ObserveVisibleDevices(controller),
            observeReadiness = ObserveBleReadiness(blocked),
        )

        model.state.test {
            val state = awaitUntil { it.blocker != null }
            assertEquals(BleFailure.PermissionDenied, state.blocker)
            assertFalse(state.canScan)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `scan failure outranks readiness in the banner`() = runViewModelTest {
        val model = viewModel()

        model.state.test {
            awaitItem()
            controller.setStatus(ScanStatus.Failed(BleFailure.ScanRejected(code = 2)))

            assertEquals(
                BleFailure.ScanRejected(2),
                awaitUntil { it.blocker != null }.blocker,
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `filter sheet opens and closes`() = runViewModelTest {
        val model = viewModel()

        model.state.test {
            awaitItem()
            model.onEvent(ScannerAction.OpenFilters)
            assertTrue(awaitItem().areFiltersOpen)

            model.onEvent(ScannerAction.DismissFilters)
            assertFalse(awaitItem().areFiltersOpen)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `stopping the scan returns the list to idle`() = runViewModelTest {
        val model = viewModel()

        model.state.test {
            awaitItem()
            model.onEvent(ScannerAction.ToggleScan)
            awaitItem()
            controller.emit(listOf(TestDevices.device()))
            awaitUntil { it.content is ScannerContent.Found }

            model.onEvent(ScannerAction.ToggleScan)
            assertEquals(ScannerContent.Idle, awaitUntil { it.content is ScannerContent.Idle }.content)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
