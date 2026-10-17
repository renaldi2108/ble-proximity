package id.renaldi.proximity.core.designsystem.foundation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import id.renaldi.proximity.core.model.ProximityZone

val ProximityZone.accentColor: Color
    get() = when (this) {
        ProximityZone.IMMEDIATE -> SignalImmediate
        ProximityZone.NEAR -> SignalNear
        ProximityZone.GOOD -> SignalGood
        ProximityZone.WEAK -> SignalWeak
        ProximityZone.VERY_WEAK -> SignalVeryWeak
        ProximityZone.LOST -> SignalLost
    }

val ProximityZone.label: String
    get() = when (this) {
        ProximityZone.IMMEDIATE -> "Sangat Kuat"
        ProximityZone.NEAR -> "Kuat"
        ProximityZone.GOOD -> "Cukup"
        ProximityZone.WEAK -> "Lemah"
        ProximityZone.VERY_WEAK -> "Sangat Lemah"
        ProximityZone.LOST -> "Sinyal Hilang"
    }

val ProximityZone.proximityLabel: String
    get() = when (this) {
        ProximityZone.IMMEDIATE -> "Sangat dekat"
        ProximityZone.NEAR -> "Dekat"
        ProximityZone.GOOD -> "Sedang"
        ProximityZone.WEAK -> "Jauh"
        ProximityZone.VERY_WEAK -> "Batas jangkauan"
        ProximityZone.LOST -> "Di luar jangkauan"
    }

val ProximityZone.filledBars: Int
    get() = when (this) {
        ProximityZone.IMMEDIATE -> MAX_BARS
        ProximityZone.NEAR -> 4
        ProximityZone.GOOD -> 3
        ProximityZone.WEAK -> 2
        ProximityZone.VERY_WEAK -> 1
        ProximityZone.LOST -> 0
    }

const val MAX_BARS: Int = 5

@Composable
fun ProximityZone.containerColor(alpha: Float = CONTAINER_ALPHA): Color =
    accentColor.copy(alpha = alpha)

private const val CONTAINER_ALPHA = 0.14f
