package id.renaldi.proximity.core.designsystem.atom

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp
import id.renaldi.proximity.core.designsystem.foundation.AppDimens
import id.renaldi.proximity.core.designsystem.foundation.MAX_BARS
import id.renaldi.proximity.core.designsystem.foundation.accentColor
import id.renaldi.proximity.core.designsystem.foundation.filledBars
import id.renaldi.proximity.core.model.ProximityZone

@Composable
fun SignalBars(
    zone: ProximityZone,
    modifier: Modifier = Modifier,
    barWidth: Dp = AppDimens.signalBarWidth,
    barGap: Dp = AppDimens.signalBarGap,
    maxHeight: Dp = AppDimens.signalBarMaxHeight,
) {
    val accent = zone.accentColor
    val idle = MaterialTheme.colorScheme.outline
    val filled = zone.filledBars
    val width = barWidth * MAX_BARS + barGap * (MAX_BARS - 1)

    Spacer(
        modifier = modifier
            .size(width = width, height = maxHeight)
            .drawWithCache {
                val gap = barGap.toPx()
                val bar = barWidth.toPx()
                val corner = CornerRadius(bar / 2)
                val step = size.height / MAX_BARS
                onDrawBehind {
                    repeat(MAX_BARS) { index ->
                        val barHeight = step * (index + 1)
                        drawRoundRect(
                            color = if (index < filled) accent else idle,
                            topLeft = Offset(
                                x = index * (bar + gap),
                                y = size.height - barHeight,
                            ),
                            size = Size(width = bar, height = barHeight),
                            cornerRadius = corner,
                        )
                    }
                }
            },
    )
}
