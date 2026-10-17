package id.renaldi.proximity.feature.scanner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import id.renaldi.proximity.core.common.format.DistanceFormat
import id.renaldi.proximity.core.designsystem.atom.MonoText
import id.renaldi.proximity.core.designsystem.foundation.AppDimens
import id.renaldi.proximity.core.designsystem.foundation.AppSpacing
import id.renaldi.proximity.core.designsystem.foundation.Strings
import id.renaldi.proximity.core.designsystem.organism.BleFailureNotice
import id.renaldi.proximity.core.designsystem.organism.DeviceRow
import id.renaldi.proximity.core.designsystem.organism.EmptyState
import id.renaldi.proximity.core.designsystem.organism.ScanToggleButton
import id.renaldi.proximity.core.designsystem.template.AppScaffold
import id.renaldi.proximity.core.model.BleDevice
import id.renaldi.proximity.feature.scanner.component.FilterSheet
import id.renaldi.proximity.feature.scanner.component.ScannerFilterBar

@Composable
fun ScannerScreen(
    state: ScannerUiState,
    onAction: (ScannerAction) -> Unit,
    onNavigate: (ScannerNavigation) -> Unit,
    onEnableBluetooth: () -> Unit,
    onRequestPermission: () -> Unit,
    onOpenLocationSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppScaffold(
        title = Strings.APP_NAME,
        subtitle = state.subtitle(),
        modifier = modifier,
        actions = {
            IconButton(onClick = { onNavigate(ScannerNavigation.OpenHistory) }) {
                Icon(imageVector = Icons.Filled.History, contentDescription = Strings.HISTORY_TITLE)
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = AppDimens.pageMargin,
                    vertical = AppSpacing.md,
                ),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
            ) {
                ScannerFilterBar(filter = state.filter, onAction = onAction)

                state.blocker?.let { failure ->
                    BleFailureNotice(
                        failure = failure,
                        onEnableBluetooth = onEnableBluetooth,
                        onRequestPermission = onRequestPermission,
                        onOpenLocationSettings = onOpenLocationSettings,
                        onRetry = { onAction(ScannerAction.Retry) },
                    )
                }

                ScanToggleButton(
                    isScanning = state.isScanning,
                    enabled = state.canScan,
                    onToggle = { onAction(ScannerAction.ToggleScan) },
                )
            }

            HorizontalDivider()

            when (val content = state.content) {
                ScannerContent.Idle -> EmptyState(
                    icon = Icons.Filled.BluetoothSearching,
                    title = Strings.SCAN_IDLE_TITLE,
                    body = Strings.SCAN_IDLE_BODY,
                )

                ScannerContent.Searching -> EmptyState(
                    icon = Icons.Filled.BluetoothSearching,
                    title = Strings.SCAN_EMPTY_TITLE,
                    body = Strings.SCAN_EMPTY_BODY,
                )

                is ScannerContent.NoMatch -> EmptyState(
                    icon = Icons.Filled.SearchOff,
                    title = Strings.SCAN_FILTERED_EMPTY_TITLE,
                    body = Strings.SCAN_FILTERED_EMPTY_BODY,
                )

                is ScannerContent.Found -> DeviceList(
                    devices = content.result.devices,
                    bottomPadding = innerPadding,
                    onSelect = { onNavigate(ScannerNavigation.OpenTracker(it)) },
                )
            }
        }

        if (state.areFiltersOpen) {
            FilterSheet(filter = state.filter, onAction = onAction)
        }
    }
}

@Composable
private fun DeviceList(
    devices: List<BleDevice>,
    bottomPadding: PaddingValues,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) = LazyColumn(
    modifier = modifier.fillMaxSize(),
    contentPadding = PaddingValues(bottom = bottomPadding.calculateBottomPadding() + AppSpacing.xxl),
) {
    items(items = devices, key = BleDevice::address, contentType = { DEVICE_ROW }) { device ->
        DeviceRow(
            title = device.displayName,
            address = device.address,
            zone = device.proximity.zone,
            primaryMetric = DistanceFormat.format(device.proximity),
            secondaryMetric = DistanceFormat.formatRange(device.proximity.zone),
            onClick = { onSelect(device.address) },
            trailing = { MonoText(text = "${device.rawRssi} dBm") },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

private fun ScannerUiState.subtitle(): String = when {
    !isScanning && content is ScannerContent.Idle -> Strings.SCAN_IDLE_TITLE
    hiddenCount > 0 -> "$visibleCount ${Strings.DEVICES_FOUND} · $hiddenCount ${Strings.HIDDEN_BY_FILTER}"
    else -> "$visibleCount ${Strings.DEVICES_FOUND}"
}

private const val DEVICE_ROW = "deviceRow"
