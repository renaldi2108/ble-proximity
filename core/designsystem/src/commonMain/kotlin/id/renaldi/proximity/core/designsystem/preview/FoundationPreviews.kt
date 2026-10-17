package id.renaldi.proximity.core.designsystem.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import id.renaldi.proximity.core.common.format.DistanceFormat
import id.renaldi.proximity.core.designsystem.atom.AppText
import id.renaldi.proximity.core.designsystem.atom.ZoneDot
import id.renaldi.proximity.core.designsystem.foundation.AppDimens
import id.renaldi.proximity.core.designsystem.foundation.AppShape
import id.renaldi.proximity.core.designsystem.foundation.AppSpacing
import id.renaldi.proximity.core.designsystem.foundation.accentColor
import id.renaldi.proximity.core.designsystem.foundation.label
import id.renaldi.proximity.core.designsystem.foundation.proximityLabel
import id.renaldi.proximity.core.model.ProximityZone

@Preview(name = "Foundation · palet zona", widthDp = 393)
@Composable
private fun ZonePalettePreview() = PreviewSurface {
    ProximityZone.entries.forEach { zone -> ZoneSwatch(zone) }
}

@Preview(name = "Foundation · palet zona gelap", widthDp = 393)
@Composable
private fun ZonePaletteDarkPreview() = PreviewSurface(darkTheme = true) {
    ProximityZone.entries.forEach { zone -> ZoneSwatch(zone) }
}

@Composable
private fun ZoneSwatch(zone: ProximityZone) = Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
    verticalAlignment = Alignment.CenterVertically,
) {
    Spacer(
        modifier = Modifier
            .size(AppSpacing.xxl)
            .background(color = zone.accentColor, shape = AppShape.chip),
    )
    Column(modifier = Modifier.weight(1f)) {
        AppText(text = zone.label, style = MaterialTheme.typography.titleMedium)
        AppText(
            text = "${zone.proximityLabel} · ${DistanceFormat.formatRange(zone)}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
    ZoneDot(zone = zone, size = AppDimens.iconMd)
}

@Preview(name = "Foundation · tipografi", widthDp = 393)
@Composable
private fun TypographyPreview() = PreviewSurface {
    val type = MaterialTheme.typography
    AppText(text = "displaySmall · 3,0 m", style = type.displaySmall)
    AppText(text = "headlineMedium · 3,0 m", style = type.headlineMedium)
    AppText(text = "titleLarge · Proximity", style = type.titleLarge)
    AppText(text = "titleMedium · Galaxy Buds Pro", style = type.titleMedium)
    AppText(text = "bodyLarge · teks isi", style = type.bodyLarge)
    AppText(text = "bodyMedium · teks isi", style = type.bodyMedium)
    AppText(text = "bodySmall · keterangan", style = type.bodySmall)
    AppText(text = "labelLarge · AKSI", style = type.labelLarge)
    AppText(text = "labelSmall · C4:1A:9E:72:00:11", style = type.labelSmall)
}

@Preview(name = "Foundation · skala spasi", widthDp = 393)
@Composable
private fun SpacingPreview() = PreviewSurface {
    listOf(
        "xs" to AppSpacing.xs,
        "sm" to AppSpacing.sm,
        "md" to AppSpacing.md,
        "lg" to AppSpacing.lg,
        "xl" to AppSpacing.xl,
        "xxl" to AppSpacing.xxl,
        "huge" to AppSpacing.huge,
    ).forEach { (name, value) ->
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppText(text = name, style = MaterialTheme.typography.labelMedium)
            Spacer(
                modifier = Modifier
                    .height(AppSpacing.sm)
                    .width(value)
                    .background(MaterialTheme.colorScheme.primary, AppShape.chip),
            )
        }
    }
}
