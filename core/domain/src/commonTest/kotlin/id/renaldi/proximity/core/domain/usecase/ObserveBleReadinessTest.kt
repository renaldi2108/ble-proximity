package id.renaldi.proximity.core.domain.usecase

import app.cash.turbine.test
import id.renaldi.proximity.core.model.BleFailure
import id.renaldi.proximity.core.model.BleReadiness
import id.renaldi.proximity.core.testing.FakeBleEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

class ObserveBleReadinessTest {
    @Test
    fun `current readiness is emitted before any change arrives`() = runTest {
        val environment = FakeBleEnvironment(BleReadiness.Blocked(BleFailure.PermissionDenied))

        ObserveBleReadiness(environment)().test {
            assertEquals(BleReadiness.Blocked(BleFailure.PermissionDenied), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `repeated identical states are collapsed`() = runTest {
        val environment = FakeBleEnvironment(BleReadiness.Ready)

        ObserveBleReadiness(environment)().test {
            assertEquals(BleReadiness.Ready, awaitItem())

            environment.set(BleReadiness.Ready)
            expectNoEvents()

            environment.set(BleReadiness.Blocked(BleFailure.BluetoothOff))
            assertEquals(BleReadiness.Blocked(BleFailure.BluetoothOff), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
