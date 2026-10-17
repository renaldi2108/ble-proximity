package id.renaldi.proximity.core.designsystem.organism

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.unit.dp
import id.renaldi.proximity.core.designsystem.atom.AppText
import id.renaldi.proximity.core.designsystem.foundation.AppShape
import id.renaldi.proximity.core.designsystem.foundation.AppSpacing
import id.renaldi.proximity.core.designsystem.foundation.Strings

@Composable
fun ScanToggleButton(
    isScanning: Boolean,
    enabled: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    Button(
        onClick = onToggle,
        modifier = modifier.fillMaxWidth().height(BUTTON_HEIGHT),
        enabled = enabled,
        shape = AppShape.pill,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isScanning) scheme.errorContainer else scheme.primary,
            contentColor = if (isScanning) scheme.onErrorContainer else scheme.onPrimary,
        ),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = if (isScanning) Icons.Filled.Stop else Icons.Filled.BluetoothSearching,
                contentDescription = null,
            )
            AppText(
                text = if (isScanning) Strings.SCAN_STOP else Strings.SCAN_START,
                style = MaterialTheme.typography.labelLarge,
            )
            if (isScanning) ScanningPulse()
        }
    }
}

@Composable
private fun ScanningPulse() {
    val color = MaterialTheme.colorScheme.onErrorContainer
    val phase = rememberInfiniteTransition(label = "scanning").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(PULSE_MILLIS, easing = LinearEasing)),
        label = "phase",
    )

    Spacer(
        modifier = Modifier
            .size(PULSE_SIZE)
            .drawWithCache {
                val radius = size.minDimension / 2f
                onDrawBehind {
                    drawCircle(color = color, radius = radius * DOT_SCALE)
                    drawCircle(
                        color = color.copy(alpha = (1f - phase.value) * HALO_ALPHA),
                        radius = radius * (DOT_SCALE + phase.value * HALO_SPREAD),
                    )
                }
            },
    )
}

private val BUTTON_HEIGHT = 52.dp
private val PULSE_SIZE = 12.dp
private const val PULSE_MILLIS = 1_200
private const val DOT_SCALE = 0.4f
private const val HALO_ALPHA = 0.6f
private const val HALO_SPREAD = 0.6f
