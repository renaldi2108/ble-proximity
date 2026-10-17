package id.renaldi.proximity.feature.scanner.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import id.renaldi.proximity.core.designsystem.atom.AppText
import id.renaldi.proximity.core.designsystem.atom.SelectableChip
import id.renaldi.proximity.core.designsystem.foundation.AppSpacing
import id.renaldi.proximity.core.designsystem.foundation.Strings
import id.renaldi.proximity.core.designsystem.molecule.SectionHeader
import id.renaldi.proximity.core.designsystem.molecule.ThresholdSlider
import id.renaldi.proximity.core.designsystem.template.AppBottomSheet
import id.renaldi.proximity.core.model.DeviceFilter
import id.renaldi.proximity.core.model.DeviceSort
import id.renaldi.proximity.feature.scanner.ScannerAction

@Composable
fun FilterSheet(
    filter: DeviceFilter,
    onAction: (ScannerAction) -> Unit,
    modifier: Modifier = Modifier,
) = AppBottomSheet(
    title = Strings.FILTER_TITLE,
    onDismiss = { onAction(ScannerAction.DismissFilters) },
    modifier = modifier,
) {
    ThresholdSlider(
        label = Strings.FILTER_THRESHOLD,
        valueLabel = filter.thresholdLabel(),
        value = filter.minRssi,
        range = DeviceFilter.RSSI_THRESHOLD_RANGE,
        onValueChange = { onAction(ScannerAction.ThresholdChanged(it)) },
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppText(text = Strings.FILTER_ONLY_NAMED, style = MaterialTheme.typography.bodyMedium)
        Switch(
            checked = filter.onlyNamed,
            onCheckedChange = { onAction(ScannerAction.OnlyNamedChanged(it)) },
        )
    }

    SectionHeader(title = Strings.FILTER_SORT)
    Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
        DeviceSort.entries.forEach { sort ->
            SelectableChip(
                label = sort.label(),
                selected = filter.sort == sort,
                onClick = { onAction(ScannerAction.SortChanged(sort)) },
            )
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(onClick = { onAction(ScannerAction.ResetFilter) }) {
            AppText(text = Strings.FILTER_RESET, style = MaterialTheme.typography.labelLarge)
        }
        TextButton(onClick = { onAction(ScannerAction.DismissFilters) }) {
            AppText(
                text = Strings.FILTER_APPLY,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

internal fun DeviceFilter.thresholdLabel(): String =
    if (minRssi == DeviceFilter.NO_THRESHOLD) Strings.FILTER_THRESHOLD_OFF else "≥ $minRssi dBm"

internal fun DeviceSort.label(): String = when (this) {
    DeviceSort.STRONGEST_SIGNAL -> Strings.SORT_STRONGEST
    DeviceSort.RECENTLY_SEEN -> Strings.SORT_RECENT
    DeviceSort.NAME -> Strings.SORT_NAME
}
