package id.renaldi.proximity.core.designsystem.organism

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.unit.Dp
import id.renaldi.proximity.core.designsystem.foundation.AppDimens
import id.renaldi.proximity.core.designsystem.foundation.accentColor
import id.renaldi.proximity.core.model.ProximityZone

@Composable
fun RssiSparkline(
    samples: List<Int>,
    zone: ProximityZone,
    modifier: Modifier = Modifier,
    height: Dp = AppDimens.sparklineHeight,
) {
    val accent = zone.accentColor
    val baseline = MaterialTheme.colorScheme.outlineVariant

    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .drawWithCache {
                val stroke = Stroke(width = STROKE_PX, cap = StrokeCap.Round)
                val path = samples.toPath(size.width, size.height, STROKE_PX)
                onDrawBehind {
                    drawLine(
                        color = baseline,
                        start = Offset(0f, size.height / 2),
                        end = Offset(size.width, size.height / 2),
                        strokeWidth = HAIRLINE_PX,
                    )
                    if (path != null) drawPath(path = path, color = accent, style = stroke)
                }
            },
    )
}

private fun List<Int>.toPath(width: Float, height: Float, inset: Float): Path? {
    if (size < 2) return null
    val lowest = min()
    val highest = max()
    val center = (lowest + highest) / 2f
    val span = maxOf((highest - lowest).toFloat(), MIN_SPAN_DB)
    val floor = center - span / 2f
    val usable = height - inset * 2
    val step = width / (size - 1)
    val path = Path()
    forEachIndexed { index, rssi ->
        val ratio = ((rssi - floor) / span).coerceIn(0f, 1f)
        path.run {
            val x = index * step
            val y = inset + usable - usable * ratio
            if (index == 0) moveTo(x, y) else lineTo(x, y)
        }
    }
    return path
}

private const val MIN_SPAN_DB = 8f
private const val STROKE_PX = 2.5f
private const val HAIRLINE_PX = 1f
