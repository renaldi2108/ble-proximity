package id.renaldi.proximity.feature.scanner.domain

import app.cash.turbine.test
import id.renaldi.proximity.core.model.DeviceFilter
import id.renaldi.proximity.core.model.DeviceSort
import id.renaldi.proximity.core.testing.FakeScanController
import id.renaldi.proximity.core.testing.TestDevices
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest

class ObserveVisibleDevicesTest {
    private val controller = FakeScanController()
    private val observe = ObserveVisibleDevices(controller)

    private val buds = TestDevices.device(TestDevices.BUDS, "Galaxy Buds Pro", rssi = -42)
    private val speaker = TestDevices.device(TestDevices.SPEAKER, "JBL Flip 6", rssi = -58)
    private val anonymous = TestDevices.device(TestDevices.ANONYMOUS, null, rssi = -84)

    @Test
    fun `strongest signal leads the list without being asked`() = runTest {
        val filters = MutableStateFlow(DeviceFilter.Default)

        observe(filters).test {
            controller.emit(listOf(speaker, anonymous, buds))
            awaitItem()
            val result = awaitItem()

            assertEquals(
                listOf(TestDevices.BUDS, TestDevices.SPEAKER, TestDevices.ANONYMOUS),
                result.devices.map { it.address },
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `threshold filter reports how many devices it hid`() = runTest {
        val filters = MutableStateFlow(DeviceFilter(minRssi = -70))

        observe(filters).test {
            controller.emit(listOf(speaker, anonymous, buds))
            awaitItem()
            val result = awaitItem()

            assertEquals(2, result.visibleCount)
            assertEquals(3, result.totalCount)
            assertEquals(1, result.hiddenCount)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `query narrows the list without restarting the scan`() = runTest {
        val filters = MutableStateFlow(DeviceFilter.Default)

        observe(filters).test {
            controller.emit(listOf(speaker, anonymous, buds))
            awaitItem()
            assertEquals(3, awaitItem().devices.size)

            filters.value = DeviceFilter(query = "jbl")
            assertEquals(listOf(TestDevices.SPEAKER), awaitItem().devices.map { it.address })
            assertEquals(0, controller.stopCount)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `name sort puts unnamed devices last`() = runTest {
        val filters = MutableStateFlow(DeviceFilter(sort = DeviceSort.NAME))

        observe(filters).test {
            controller.emit(listOf(speaker, anonymous, buds))
            awaitItem()
            val result = awaitItem()

            assertEquals(
                listOf(TestDevices.BUDS, TestDevices.SPEAKER, TestDevices.ANONYMOUS),
                result.devices.map { it.address },
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `recently seen sort uses the last packet timestamp`() = runTest {
        val filters = MutableStateFlow(DeviceFilter(sort = DeviceSort.RECENTLY_SEEN))
        val stale = TestDevices.device(TestDevices.SPEAKER, "JBL Flip 6", lastSeenAtMillis = 1_000)
        val fresh = TestDevices.device(TestDevices.BUDS, "Galaxy Buds Pro", lastSeenAtMillis = 9_000)

        observe(filters).test {
            controller.emit(listOf(stale, fresh))
            awaitItem()
            val result = awaitItem()

            assertEquals(listOf(TestDevices.BUDS, TestDevices.SPEAKER), result.devices.map { it.address })
            cancelAndIgnoreRemainingEvents()
        }
    }
}
