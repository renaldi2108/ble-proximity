package id.renaldi.proximity.core.designsystem.foundation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.Remove
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import id.renaldi.proximity.core.model.SignalStability
import id.renaldi.proximity.core.model.SignalTrend

val SignalTrend.label: String
    get() = when (this) {
        SignalTrend.APPROACHING -> "Mendekat"
        SignalTrend.STABLE -> "Stabil"
        SignalTrend.RECEDING -> "Menjauh"
        SignalTrend.LOST -> "Hilang"
    }

val SignalTrend.icon: ImageVector
    get() = when (this) {
        SignalTrend.APPROACHING -> Icons.Filled.ArrowUpward
        SignalTrend.STABLE -> Icons.Filled.Remove
        SignalTrend.RECEDING -> Icons.Filled.ArrowDownward
        SignalTrend.LOST -> Icons.Filled.LinkOff
    }

val SignalTrend.accentColor: Color
    get() = when (this) {
        SignalTrend.APPROACHING -> SignalImmediate
        SignalTrend.STABLE -> SignalGood
        SignalTrend.RECEDING -> SignalWeak
        SignalTrend.LOST -> SignalLost
    }

val SignalStability.Grade.label: String
    get() = when (this) {
        SignalStability.Grade.STABLE -> "Stabil"
        SignalStability.Grade.UNSTABLE -> "Tidak stabil"
        SignalStability.Grade.DROPPED -> "Terputus"
    }

val SignalStability.Grade.accentColor: Color
    get() = when (this) {
        SignalStability.Grade.STABLE -> SignalImmediate
        SignalStability.Grade.UNSTABLE -> SignalWeak
        SignalStability.Grade.DROPPED -> SignalVeryWeak
    }
