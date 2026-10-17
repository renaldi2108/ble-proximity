package id.renaldi.proximity.core.designsystem.atom

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun LoadingSkeleton(
    modifier: Modifier = Modifier,
    height: Dp = 16.dp,
) {
    val base = MaterialTheme.colorScheme.surfaceContainerHigh
    val shimmer = rememberInfiniteTransition(label = "skeleton").animateFloat(
        initialValue = MIN_ALPHA,
        targetValue = MAX_ALPHA,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = CYCLE_MILLIS, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "alpha",
    )

    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .drawWithCache {
                val corner = CornerRadius(size.height / 2f)
                onDrawBehind {
                    drawRoundRect(color = base, alpha = shimmer.value, cornerRadius = corner)
                }
            },
    )
}

private const val MIN_ALPHA = 0.35f
private const val MAX_ALPHA = 0.9f
private const val CYCLE_MILLIS = 900
