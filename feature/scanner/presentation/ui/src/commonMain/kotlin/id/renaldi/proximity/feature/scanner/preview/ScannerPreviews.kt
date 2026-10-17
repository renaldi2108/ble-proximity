package id.renaldi.proximity.feature.scanner.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import id.renaldi.proximity.core.designsystem.preview.PreviewScreen
import id.renaldi.proximity.core.designsystem.preview.PreviewSurface
import id.renaldi.proximity.core.fixtures.PreviewSamples
import id.renaldi.proximity.core.model.BleFailure
import id.renaldi.proximity.core.model.DeviceFilter
import id.renaldi.proximity.core.model.DeviceSort
import id.renaldi.proximity.feature.scanner.ScannerContent
import id.renaldi.proximity.feature.scanner.ScannerScreen
import id.renaldi.proximity.feature.scanner.ScannerUiState
import id.renaldi.proximity.feature.scanner.component.FilterSheet
import id.renaldi.proximity.feature.scanner.component.ScannerFilterBar

@Preview(name = "Scanner · belum memindai", widthDp = 393, heightDp = 800)
@Composable
private fun ScannerIdlePreview() = ScannerPreview(ScannerUiState())

@Preview(name = "Scanner · memindai, belum ada hasil", widthDp = 393, heightDp = 800)
@Composable
private fun ScannerSearchingPreview() = ScannerPreview(
    ScannerUiState(content = ScannerContent.Searching, isScanning = true),
)

@Preview(name = "Scanner · daftar perangkat", widthDp = 393, heightDp = 800)
@Composable
private fun ScannerFoundPreview() = ScannerPreview(
    ScannerUiState(
        content = ScannerContent.Found(PreviewSamples.deviceList),
        isScanning = true,
    ),
)

@Preview(name = "Scanner · daftar perangkat gelap", widthDp = 393, heightDp = 800)
@Composable
private fun ScannerFoundDarkPreview() = ScannerPreview(
    state = ScannerUiState(
        content = ScannerContent.Found(PreviewSamples.deviceList),
        isScanning = true,
    ),
    darkTheme = true,
)

@Preview(name = "Scanner · filter aktif", widthDp = 393, heightDp = 800)
@Composable
private fun ScannerFilteredPreview() = ScannerPreview(
    ScannerUiState(
        content = ScannerContent.Found(PreviewSamples.filteredDeviceList),
        filter = DeviceFilter(query = "pro", minRssi = -70),
        isScanning = true,
    ),
)

@Preview(name = "Scanner · filter tidak cocok", widthDp = 393, heightDp = 800)
@Composable
private fun ScannerNoMatchPreview() = ScannerPreview(
    ScannerUiState(
        content = ScannerContent.NoMatch(totalCount = PreviewSamples.devices.size),
        filter = DeviceFilter(query = "sony"),
        isScanning = true,
    ),
)

@Preview(name = "Scanner · izin ditolak", widthDp = 393, heightDp = 800)
@Composable
private fun ScannerPermissionBlockedPreview() = ScannerPreview(
    ScannerUiState(readinessFailure = BleFailure.PermissionDenied),
)

@Preview(name = "Scanner · bluetooth mati", widthDp = 393, heightDp = 800)
@Composable
private fun ScannerBluetoothOffPreview() = ScannerPreview(
    ScannerUiState(readinessFailure = BleFailure.BluetoothOff),
)

@Preview(name = "Scanner · pemindaian ditolak sistem", widthDp = 393, heightDp = 800)
@Composable
private fun ScannerScanRejectedPreview() = ScannerPreview(
    ScannerUiState(scanFailure = BleFailure.ScanRejected(code = 2)),
)

@Preview(name = "Scanner · komponen bar filter", widthDp = 393)
@Composable
private fun ScannerFilterBarPreview() = PreviewSurface {
    ScannerFilterBar(filter = DeviceFilter.Default, onAction = {})
    ScannerFilterBar(
        filter = DeviceFilter(query = "jbl", minRssi = -70, onlyNamed = true),
        onAction = {},
    )
}

@Preview(name = "Scanner · komponen sheet filter", widthDp = 393, heightDp = 640)
@Composable
private fun FilterSheetPreview() = PreviewScreen {
    FilterSheet(
        filter = DeviceFilter(minRssi = -70, onlyNamed = true, sort = DeviceSort.NAME),
        onAction = {},
    )
}

@Composable
private fun ScannerPreview(
    state: ScannerUiState,
    darkTheme: Boolean = false,
) = PreviewScreen(darkTheme = darkTheme) {
    ScannerScreen(
        state = state,
        onAction = {},
        onNavigate = {},
        onEnableBluetooth = {},
        onRequestPermission = {},
        onOpenLocationSettings = {},
    )
}
