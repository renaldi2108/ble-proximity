package id.renaldi.proximity.core.designsystem.organism

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import id.renaldi.proximity.core.designsystem.atom.AppChip
import id.renaldi.proximity.core.designsystem.atom.AppText
import id.renaldi.proximity.core.designsystem.atom.MonoText
import id.renaldi.proximity.core.designsystem.atom.SignalBars
import id.renaldi.proximity.core.designsystem.atom.ZoneDot
import id.renaldi.proximity.core.designsystem.foundation.AppDimens
import id.renaldi.proximity.core.designsystem.foundation.AppSpacing
import id.renaldi.proximity.core.designsystem.foundation.accentColor
import id.renaldi.proximity.core.designsystem.foundation.label
import id.renaldi.proximity.core.model.ProximityZone

@Composable
fun DeviceRow(
    title: String,
    address: String,
    zone: ProximityZone,
    primaryMetric: String,
    secondaryMetric: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    val content: @Composable () -> Unit = {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = AppDimens.rowMinHeight)
                .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.md),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SignalBars(zone = zone)

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
            ) {
                AppText(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                )
                MonoText(text = address)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AppChip(
                        label = zone.label,
                        accent = zone.accentColor,
                        leading = { ZoneDot(zone = zone) },
                    )
                    AppText(
                        text = secondaryMetric,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
            ) {
                AppText(
                    text = primaryMetric,
                    style = MaterialTheme.typography.titleMedium,
                    color = zone.accentColor,
                    maxLines = 1,
                )
                trailing?.invoke()
            }
        }
    }

    if (onClick == null) {
        Surface(modifier = modifier, color = Color.Transparent, content = content)
    } else {
        Surface(modifier = modifier, onClick = onClick, color = Color.Transparent, content = content)
    }
}
