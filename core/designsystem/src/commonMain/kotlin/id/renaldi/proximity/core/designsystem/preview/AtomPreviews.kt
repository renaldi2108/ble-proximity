package id.renaldi.proximity.core.designsystem.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import id.renaldi.proximity.core.designsystem.atom.AppChip
import id.renaldi.proximity.core.designsystem.atom.AppText
import id.renaldi.proximity.core.designsystem.atom.LoadingSkeleton
import id.renaldi.proximity.core.designsystem.atom.MonoText
import id.renaldi.proximity.core.designsystem.atom.SelectableChip
import id.renaldi.proximity.core.designsystem.atom.SignalBars
import id.renaldi.proximity.core.designsystem.atom.ZoneDot
import id.renaldi.proximity.core.designsystem.foundation.AppSpacing
import id.renaldi.proximity.core.designsystem.foundation.accentColor
import id.renaldi.proximity.core.designsystem.foundation.label
import id.renaldi.proximity.core.model.DeviceSort
import id.renaldi.proximity.core.model.ProximityZone

@Preview(name = "Atom · teks", widthDp = 393)
@Composable
private fun AppTextPreview() = PreviewSurface {
    AppText(text = "Galaxy Buds Pro", style = MaterialTheme.typography.titleMedium)
    AppText(
        text = "Nama perangkat yang sangat panjang sehingga harus dipotong di satu baris saja",
        style = MaterialTheme.typography.bodyMedium,
        maxLines = 1,
    )
    MonoText(text = "C4:1A:9E:72:00:11")
    MonoText(text = "-59 dBm")
}

@Preview(name = "Atom · bar sinyal", widthDp = 393)
@Composable
private fun SignalBarsPreview() = PreviewSurface {
    ProximityZone.entries.forEach { zone ->
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SignalBars(zone = zone)
            AppText(text = zone.label, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview(name = "Atom · bar sinyal gelap", widthDp = 393)
@Composable
private fun SignalBarsDarkPreview() = PreviewSurface(darkTheme = true) {
    ProximityZone.entries.forEach { zone ->
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SignalBars(zone = zone)
            AppText(text = zone.label, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview(name = "Atom · titik zona", widthDp = 393)
@Composable
private fun ZoneDotPreview() = PreviewSurface {
    Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
        ProximityZone.entries.forEach { zone -> ZoneDot(zone = zone) }
    }
}

@Preview(name = "Atom · chip", widthDp = 393)
@Composable
private fun AppChipPreview() = PreviewSurface {
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
        ProximityZone.entries.forEach { zone ->
            AppChip(
                label = zone.label,
                accent = zone.accentColor,
                leading = { ZoneDot(zone = zone) },
            )
        }
        AppChip(
            label = "Memindai",
            leading = { Icon(Icons.Filled.BluetoothSearching, contentDescription = null) },
        )
    }
}

@Preview(name = "Atom · chip pilihan", widthDp = 393)
@Composable
private fun SelectableChipPreview() = PreviewSurface {
    var selected by remember { mutableStateOf(DeviceSort.STRONGEST_SIGNAL) }
    Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
        DeviceSort.entries.forEach { sort ->
            SelectableChip(
                label = sort.name,
                selected = sort == selected,
                onClick = { selected = sort },
            )
        }
    }
}

@Preview(name = "Atom · skeleton", widthDp = 393)
@Composable
private fun LoadingSkeletonPreview() = PreviewSurface {
    LoadingSkeleton()
    LoadingSkeleton(height = AppSpacing.xxl)
}
