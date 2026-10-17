package id.renaldi.proximity.feature.scanner.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.renaldi.proximity.core.designsystem.foundation.AppSpacing
import id.renaldi.proximity.core.designsystem.foundation.Strings
import id.renaldi.proximity.core.designsystem.molecule.SearchField
import id.renaldi.proximity.core.model.DeviceFilter
import id.renaldi.proximity.feature.scanner.ScannerAction

@Composable
fun ScannerFilterBar(
    filter: DeviceFilter,
    onAction: (ScannerAction) -> Unit,
    modifier: Modifier = Modifier,
) = Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
    verticalAlignment = Alignment.CenterVertically,
) {
    SearchField(
        value = filter.query,
        onValueChange = { onAction(ScannerAction.QueryChanged(it)) },
        placeholder = Strings.SEARCH_PLACEHOLDER,
        modifier = Modifier.weight(1f),
    )
    Box {
        IconButton(onClick = { onAction(ScannerAction.OpenFilters) }) {
            Icon(
                imageVector = Icons.Filled.FilterList,
                contentDescription = Strings.FILTER_TITLE,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        val badgeCount = filter.activeCount - if (filter.query.isNotBlank()) 1 else 0
        if (badgeCount > 0) {
            Badge(modifier = Modifier.align(Alignment.TopEnd).offset(x = (-4).dp, y = 4.dp)) {
                androidx.compose.material3.Text(text = badgeCount.toString())
            }
        }
    }
}
