package id.renaldi.proximity.core.designsystem.atom

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import id.renaldi.proximity.core.designsystem.foundation.AppDimens
import id.renaldi.proximity.core.designsystem.foundation.AppShape
import id.renaldi.proximity.core.designsystem.foundation.AppSpacing

@Composable
fun AppChip(
    label: String,
    modifier: Modifier = Modifier,
    accent: Color? = null,
    leading: @Composable (() -> Unit)? = null,
    shape: RoundedCornerShape = AppShape.chip,
) {
    val tint = accent ?: MaterialTheme.colorScheme.onSurfaceVariant
    Surface(
        modifier = modifier,
        shape = shape,
        color = Color.Transparent,
        border = BorderStroke(width = AppDimens.hairline, color = tint.copy(alpha = BORDER_ALPHA)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = AppSpacing.sm, vertical = AppSpacing.xs),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            leading?.invoke()
            AppText(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = tint,
                maxLines = 1,
            )
        }
    }
}

@Composable
fun SelectableChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    Surface(
        modifier = modifier,
        onClick = onClick,
        shape = AppShape.chip,
        color = if (selected) scheme.primaryContainer else Color.Transparent,
        border = BorderStroke(
            width = AppDimens.hairline,
            color = if (selected) scheme.primary else scheme.outline,
        ),
    ) {
        AppText(
            text = label,
            modifier = Modifier.padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) scheme.onPrimaryContainer else scheme.onSurfaceVariant,
            maxLines = 1,
        )
    }
}

private const val BORDER_ALPHA = 0.45f
