package id.renaldi.proximity.core.designsystem.preview

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import id.renaldi.proximity.core.common.format.DistanceFormat
import id.renaldi.proximity.core.common.format.RelativeTimeFormat
import id.renaldi.proximity.core.designsystem.atom.MonoText
import id.renaldi.proximity.core.designsystem.foundation.Strings
import id.renaldi.proximity.core.designsystem.foundation.proximityLabel
import id.renaldi.proximity.core.designsystem.organism.BleFailureNotice
import id.renaldi.proximity.core.designsystem.organism.DeviceRow
import id.renaldi.proximity.core.designsystem.organism.EmptyState
import id.renaldi.proximity.core.designsystem.organism.RadarGauge
import id.renaldi.proximity.core.designsystem.organism.RssiSparkline
import id.renaldi.proximity.core.designsystem.organism.ScanToggleButton
import id.renaldi.proximity.core.fixtures.PreviewSamples
import id.renaldi.proximity.core.model.BleFailure
import id.renaldi.proximity.core.model.ProximityZone

@Preview(name = "Organisme · baris perangkat (scanner)", widthDp = 393)
@Composable
private fun DeviceRowScannerPreview() = PreviewScreen {
    androidx.compose.foundation.layout.Column {
        PreviewSamples.devices.forEach { device ->
            DeviceRow(
                title = device.displayName,
                address = device.address,
                zone = device.proximity.zone,
                primaryMetric = DistanceFormat.format(device.proximity),
                secondaryMetric = DistanceFormat.formatRange(device.proximity.zone),
                onClick = {},
                trailing = { MonoText(text = "${device.rawRssi} dBm") },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(name = "Organisme · baris perangkat gelap", widthDp = 393)
@Composable
private fun DeviceRowDarkPreview() = PreviewScreen(darkTheme = true) {
    androidx.compose.foundation.layout.Column {
        PreviewSamples.devices.take(4).forEach { device ->
            DeviceRow(
                title = device.displayName,
                address = device.address,
                zone = device.proximity.zone,
                primaryMetric = DistanceFormat.format(device.proximity),
                secondaryMetric = DistanceFormat.formatRange(device.proximity.zone),
                onClick = {},
                trailing = { MonoText(text = "${device.rawRssi} dBm") },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(name = "Organisme · baris perangkat (riwayat)", widthDp = 393)
@Composable
private fun DeviceRowHistoryPreview() = PreviewScreen {
    androidx.compose.foundation.layout.Column {
        PreviewSamples.historyEntries.forEach { entry ->
            DeviceRow(
                title = entry.displayName,
                address = entry.address,
                zone = entry.lastZone,
                primaryMetric = "${entry.lastRssi} dBm",
                secondaryMetric = RelativeTimeFormat.format(
                    timestampMillis = entry.lastSeenAtMillis,
                    nowMillis = PreviewSamples.NOW_MILLIS,
                ),
                onClick = {},
                trailing = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.DeleteOutline, contentDescription = Strings.HISTORY_DELETE)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(name = "Organisme · radar per zona", widthDp = 393, heightDp = 1800)
@Composable
private fun RadarGaugeZonesPreview() = PreviewSurface {
    ProximityZone.entries.forEach { zone ->
        val device = PreviewSamples.deviceInZone(zone)
        RadarGauge(
            zone = zone,
            rangeRatio = device.proximity.rangeRatio,
            headline = DistanceFormat.format(device.proximity),
            caption = zone.proximityLabel,
            isLive = zone != ProximityZone.LOST,
        )
    }
}

@Preview(name = "Organisme · radar dekat", widthDp = 393, heightDp = 420)
@Composable
private fun RadarGaugeNearPreview() = PreviewSurface {
    val device = PreviewSamples.buds
    RadarGauge(
        zone = device.proximity.zone,
        rangeRatio = device.proximity.rangeRatio,
        headline = DistanceFormat.format(device.proximity),
        caption = device.proximity.zone.proximityLabel,
    )
}

@Preview(name = "Organisme · radar gelap", widthDp = 393, heightDp = 420)
@Composable
private fun RadarGaugeDarkPreview() = PreviewSurface(darkTheme = true) {
    val device = PreviewSamples.band
    RadarGauge(
        zone = device.proximity.zone,
        rangeRatio = device.proximity.rangeRatio,
        headline = DistanceFormat.format(device.proximity),
        caption = device.proximity.zone.proximityLabel,
    )
}

@Preview(name = "Organisme · sparkline", widthDp = 393)
@Composable
private fun RssiSparklinePreview() = PreviewSurface {
    RssiSparkline(samples = PreviewSamples.rssiTrace, zone = ProximityZone.NEAR)
    RssiSparkline(samples = PreviewSamples.trackedLost.rssiHistory, zone = ProximityZone.LOST)
    RssiSparkline(samples = listOf(-60), zone = ProximityZone.GOOD)
}

@Preview(name = "Organisme · tombol pindai", widthDp = 393)
@Composable
private fun ScanToggleButtonPreview() = PreviewSurface {
    ScanToggleButton(isScanning = false, enabled = true, onToggle = {})
    ScanToggleButton(isScanning = true, enabled = true, onToggle = {})
    ScanToggleButton(isScanning = false, enabled = false, onToggle = {})
}

@Preview(name = "Organisme · keadaan kosong", widthDp = 393)
@Composable
private fun EmptyStatePreview() = PreviewSurface {
    EmptyState(
        icon = Icons.Filled.BluetoothSearching,
        title = Strings.SCAN_IDLE_TITLE,
        body = Strings.SCAN_IDLE_BODY,
    )
    EmptyState(
        icon = Icons.Filled.SearchOff,
        title = Strings.SCAN_FILTERED_EMPTY_TITLE,
        body = Strings.SCAN_FILTERED_EMPTY_BODY,
    )
}

@Preview(name = "Organisme · semua kegagalan BLE", widthDp = 393)
@Composable
private fun BleFailureNoticePreview() = PreviewSurface {
    listOf(
        BleFailure.BluetoothOff,
        BleFailure.PermissionDenied,
        BleFailure.LocationServicesOff,
        BleFailure.Unsupported,
        BleFailure.ScanRejected(code = 2),
        BleFailure.Unexpected(detail = "Adapter tidak merespons"),
    ).forEach { failure ->
        BleFailureNotice(
            failure = failure,
            onEnableBluetooth = {},
            onRequestPermission = {},
            onOpenLocationSettings = {},
            onRetry = {},
        )
    }
}
