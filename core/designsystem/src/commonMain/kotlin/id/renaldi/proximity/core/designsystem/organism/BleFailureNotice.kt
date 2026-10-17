package id.renaldi.proximity.core.designsystem.organism

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import id.renaldi.proximity.core.designsystem.foundation.Strings
import id.renaldi.proximity.core.designsystem.molecule.NoticeBanner
import id.renaldi.proximity.core.model.BleFailure

@Composable
fun BleFailureNotice(
    failure: BleFailure,
    onEnableBluetooth: () -> Unit,
    onRequestPermission: () -> Unit,
    onOpenLocationSettings: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val copy = failure.copy()
    NoticeBanner(
        title = copy.title,
        body = copy.body,
        icon = copy.icon,
        modifier = modifier,
        accent = MaterialTheme.colorScheme.error,
        actionLabel = copy.actionLabel,
        onAction = when (failure) {
            BleFailure.BluetoothOff -> onEnableBluetooth
            BleFailure.PermissionDenied -> onRequestPermission
            BleFailure.LocationServicesOff -> onOpenLocationSettings
            BleFailure.Unsupported -> null
            is BleFailure.ScanRejected, is BleFailure.Unexpected -> onRetry
        },
    )
}

private class FailureCopy(
    val title: String,
    val body: String,
    val icon: ImageVector,
    val actionLabel: String?,
)

private fun BleFailure.copy(): FailureCopy = when (this) {
    BleFailure.BluetoothOff -> FailureCopy(
        title = Strings.BLUETOOTH_OFF_TITLE,
        body = Strings.BLUETOOTH_OFF_BODY,
        icon = Icons.Filled.BluetoothDisabled,
        actionLabel = Strings.BLUETOOTH_OFF_ACTION,
    )

    BleFailure.PermissionDenied -> FailureCopy(
        title = Strings.PERMISSION_TITLE,
        body = Strings.PERMISSION_BODY,
        icon = Icons.Filled.Lock,
        actionLabel = Strings.PERMISSION_ACTION,
    )

    BleFailure.LocationServicesOff -> FailureCopy(
        title = Strings.LOCATION_TITLE,
        body = Strings.LOCATION_BODY,
        icon = Icons.Filled.LocationOff,
        actionLabel = Strings.LOCATION_ACTION,
    )

    BleFailure.Unsupported -> FailureCopy(
        title = Strings.UNSUPPORTED_TITLE,
        body = Strings.UNSUPPORTED_BODY,
        icon = Icons.Filled.ErrorOutline,
        actionLabel = null,
    )

    is BleFailure.ScanRejected -> FailureCopy(
        title = Strings.SCAN_REJECTED_TITLE,
        body = "Sistem menolak pemindaian dengan kode $code.",
        icon = Icons.Filled.ErrorOutline,
        actionLabel = Strings.RETRY,
    )

    is BleFailure.Unexpected -> FailureCopy(
        title = Strings.SCAN_REJECTED_TITLE,
        body = detail,
        icon = Icons.Filled.ErrorOutline,
        actionLabel = Strings.RETRY,
    )
}
