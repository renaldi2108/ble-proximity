package id.renaldi.proximity.core.designsystem.organism

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.unit.dp
import id.renaldi.proximity.core.designsystem.atom.AppText
import id.renaldi.proximity.core.designsystem.foundation.AppDimens
import id.renaldi.proximity.core.designsystem.foundation.AppMotion
import id.renaldi.proximity.core.designsystem.foundation.AppSpacing
import id.renaldi.proximity.core.designsystem.foundation.accentColor
import id.renaldi.proximity.core.model.ProximityZone
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun RadarGauge(
    zone: ProximityZone,
    rangeRatio: Float,
    headline: String,
    caption: String,
    modifier: Modifier = Modifier,
    isLive: Boolean = true,
) {
    val transition = rememberInfiniteTransition(label = "radar")
    val sweep = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = AppMotion.SWEEP_MILLIS, easing = LinearEasing),
        ),
        label = "sweep",
    )
    val pulse = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = AppMotion.PULSE_MILLIS, easing = LinearEasing),
        ),
        label = "pulse",
    )
    val target = animateFloatAsState(
        targetValue = rangeRatio.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = AppMotion.DISTANCE_SETTLE_MILLIS),
        label = "range",
    )

    val accent = zone.accentColor
    val grid = MaterialTheme.colorScheme.outlineVariant
    val hub = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .defaultMinSize(minWidth = AppDimens.radarMinSize, minHeight = AppDimens.radarMinSize),
        contentAlignment = Alignment.Center,
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .drawWithCache {
                    val middle = Offset(size.width / 2f, size.height / 2f)
                    val ringStroke = AppDimens.ringStroke.toPx()
                    val targetStroke = AppDimens.targetRingStroke.toPx()
                    val blipRadius = BLIP_RADIUS.toPx()
                    val hubRadius = HUB_RADIUS.toPx()
                    val maxRadius = min(size.width, size.height) / 2f - targetStroke
                    val span = maxRadius * (1f - READOUT_WELL)
                    val well = maxRadius * READOUT_WELL
                    val rings = FloatArray(RING_COUNT) { well + span * (it + 1) / RING_COUNT }
                    val sweepBrush = Brush.sweepGradient(
                        SWEEP_START to Color.Transparent,
                        SWEEP_TAIL to accent.copy(alpha = SWEEP_TAIL_ALPHA),
                        SWEEP_END to accent.copy(alpha = SWEEP_HEAD_ALPHA),
                        center = middle,
                    )

                    onDrawBehind {
                        rings.forEach { radius ->
                            drawCircle(
                                color = grid,
                                radius = radius,
                                center = middle,
                                style = Stroke(width = ringStroke),
                            )
                        }

                        if (isLive) {
                            rotate(degrees = sweep.value * FULL_TURN, pivot = middle) {
                                drawCircle(brush = sweepBrush, radius = maxRadius, center = middle)
                            }
                        }

                        val radius = well + span * target.value
                        val phase = pulse.value

                        drawCircle(
                            color = accent.copy(alpha = HALO_ALPHA * (1f - phase)),
                            radius = radius + maxRadius * HALO_SPREAD * phase,
                            center = middle,
                            style = Stroke(width = targetStroke),
                        )
                        drawCircle(
                            color = accent,
                            radius = radius,
                            center = middle,
                            style = Stroke(width = targetStroke),
                        )

                        val blip = Offset(
                            x = middle.x + radius * cos(BLIP_ANGLE_RADIANS),
                            y = middle.y + radius * sin(BLIP_ANGLE_RADIANS),
                        )
                        drawCircle(
                            color = accent.copy(alpha = HALO_ALPHA * (1f - phase)),
                            radius = blipRadius * (1f + phase * BLIP_HALO_SCALE),
                            center = blip,
                        )
                        drawCircle(color = accent, radius = blipRadius, center = blip)
                        drawCircle(color = hub, radius = hubRadius, center = middle)
                    }
                },
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
        ) {
            AppText(
                text = headline,
                style = MaterialTheme.typography.headlineMedium,
                color = accent,
                maxLines = 1,
            )
            AppText(
                text = caption,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
        }
    }
}

private const val RING_COUNT = 5
private const val READOUT_WELL = 0.34f
private const val FULL_TURN = 360f
private const val SWEEP_START = 0f
private const val SWEEP_TAIL = 0.78f
private const val SWEEP_END = 1f
private const val SWEEP_TAIL_ALPHA = 0.04f
private const val SWEEP_HEAD_ALPHA = 0.32f
private const val HALO_ALPHA = 0.45f
private const val HALO_SPREAD = 0.1f
private const val BLIP_HALO_SCALE = 1.4f
private const val BLIP_ANGLE_RADIANS = -1.0472f

private val BLIP_RADIUS = 6.dp
private val HUB_RADIUS = 3.dp
