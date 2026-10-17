package id.renaldi.proximity.feature.tracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import id.renaldi.proximity.core.common.format.DistanceFormat
import id.renaldi.proximity.core.designsystem.atom.AppChip
import id.renaldi.proximity.core.designsystem.atom.AppText
import id.renaldi.proximity.core.designsystem.atom.MonoText
import id.renaldi.proximity.core.designsystem.foundation.AppDimens
import id.renaldi.proximity.core.designsystem.foundation.AppSpacing
import id.renaldi.proximity.core.designsystem.foundation.Strings
import id.renaldi.proximity.core.designsystem.foundation.accentColor
import id.renaldi.proximity.core.designsystem.foundation.icon
import id.renaldi.proximity.core.designsystem.foundation.label
import id.renaldi.proximity.core.designsystem.foundation.proximityLabel
import id.renaldi.proximity.core.designsystem.molecule.AppCard
import id.renaldi.proximity.core.designsystem.molecule.SectionHeader
import id.renaldi.proximity.core.designsystem.organism.BleFailureNotice
import id.renaldi.proximity.core.designsystem.organism.EmptyState
import id.renaldi.proximity.core.designsystem.organism.RadarGauge
import id.renaldi.proximity.core.designsystem.organism.RssiSparkline
import id.renaldi.proximity.core.designsystem.organism.ScanToggleButton
import id.renaldi.proximity.core.designsystem.template.AppScaffold
import id.renaldi.proximity.core.model.TrackedDevice
import id.renaldi.proximity.feature.tracker.component.DeviceFactsCard
import id.renaldi.proximity.feature.tracker.component.SignalStatsCard

@Composable
fun TrackerScreen(
    state: TrackerUiState,
    onAction: (TrackerAction) -> Unit,
    onNavigate: (TrackerNavigation) -> Unit,
    onEnableBluetooth: () -> Unit,
    onRequestPermission: () -> Unit,
    onOpenLocationSettings: () -> Unit,
    modifier: Modifier = Modifier,
) = AppScaffold(
    title = state.title,
    subtitle = state.address,
    modifier = modifier,
    onBack = { onNavigate(TrackerNavigation.Back) },
) { innerPadding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(innerPadding)
            .padding(horizontal = AppDimens.pageMargin, vertical = AppSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        state.blocker?.let { failure ->
            BleFailureNotice(
                failure = failure,
                onEnableBluetooth = onEnableBluetooth,
                onRequestPermission = onRequestPermission,
                onOpenLocationSettings = onOpenLocationSettings,
                onRetry = { onAction(TrackerAction.Retry) },
            )
        }

        val tracked = state.tracked
        if (tracked == null) {
            EmptyState(
                icon = Icons.Filled.Radar,
                title = Strings.SCAN_EMPTY_TITLE,
                body = Strings.SCAN_EMPTY_BODY,
            )
        } else {
            RadarGauge(
                zone = tracked.device.proximity.zone,
                rangeRatio = tracked.device.proximity.rangeRatio,
                headline = DistanceFormat.format(tracked.device.proximity),
                caption = tracked.device.proximity.zone.proximityLabel,
                isLive = state.isScanning && !tracked.isLost,
            )

            TrackerBadges(tracked = tracked)

            AppText(
                text = Strings.TRACK_NO_BEARING,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            AppCard(modifier = Modifier.fillMaxWidth()) {
                SectionHeader(title = Strings.TRACK_HISTORY)
                RssiSparkline(
                    samples = tracked.rssiHistory,
                    zone = tracked.device.proximity.zone,
                    modifier = Modifier.padding(top = AppSpacing.md),
                )
                MonoText(text = "${tracked.weakestRssi} dBm … ${tracked.strongestRssi} dBm")
            }

            SignalStatsCard(tracked = tracked, modifier = Modifier.fillMaxWidth())
            DeviceFactsCard(tracked = tracked, modifier = Modifier.fillMaxWidth())
        }

        ScanToggleButton(
            isScanning = state.isScanning,
            enabled = state.readinessFailure == null,
            onToggle = { onAction(TrackerAction.ToggleScan) },
        )
    }
}

@Composable
private fun TrackerBadges(tracked: TrackedDevice, modifier: Modifier = Modifier) {
    val zone = tracked.device.proximity.zone
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
    ) {
        AppChip(label = zone.label, accent = zone.accentColor)
        AppChip(
            label = tracked.trend.label,
            accent = tracked.trend.accentColor,
            leading = {
                Icon(
                    imageVector = tracked.trend.icon,
                    contentDescription = null,
                    tint = tracked.trend.accentColor,
                    modifier = Modifier.size(AppDimens.iconSm),
                )
            },
        )
    }
}
