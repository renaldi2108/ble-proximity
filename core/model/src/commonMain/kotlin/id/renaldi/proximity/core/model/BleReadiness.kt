package id.renaldi.proximity.core.model

sealed interface BleReadiness {
    data object Ready : BleReadiness

    data class Blocked(val failure: BleFailure) : BleReadiness

    val isReady: Boolean get() = this is Ready
}
