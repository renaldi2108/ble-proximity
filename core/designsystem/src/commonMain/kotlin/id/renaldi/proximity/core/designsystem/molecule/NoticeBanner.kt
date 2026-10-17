package id.renaldi.proximity.core.designsystem.molecule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import id.renaldi.proximity.core.designsystem.atom.AppText
import id.renaldi.proximity.core.designsystem.foundation.AppDimens
import id.renaldi.proximity.core.designsystem.foundation.AppShape
import id.renaldi.proximity.core.designsystem.foundation.AppSpacing

@Composable
fun NoticeBanner(
    title: String,
    body: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    accent: Color = MaterialTheme.colorScheme.error,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) = Surface(
    modifier = modifier.fillMaxWidth(),
    shape = AppShape.card,
    color = accent.copy(alpha = CONTAINER_ALPHA),
) {
    Row(
        modifier = Modifier.padding(AppSpacing.lg),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accent,
            modifier = Modifier.size(AppDimens.iconLg),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
        ) {
            AppText(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            AppText(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (actionLabel != null && onAction != null) {
                TextButton(onClick = onAction, contentPadding = ActionPadding) {
                    AppText(
                        text = actionLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = accent,
                    )
                }
            }
        }
    }
}

private val ActionPadding = PaddingValues(horizontal = AppSpacing.xs, vertical = AppSpacing.xs)

private const val CONTAINER_ALPHA = 0.12f
