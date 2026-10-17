package id.renaldi.proximity.feature.tracker.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import id.renaldi.proximity.core.common.format.DistanceFormat
import id.renaldi.proximity.core.common.format.RelativeTimeFormat
import id.renaldi.proximity.core.common.format.toFixed
import id.renaldi.proximity.core.designsystem.foundation.AppSpacing
import id.renaldi.proximity.core.designsystem.foundation.Strings
import id.renaldi.proximity.core.designsystem.foundation.accentColor
import id.renaldi.proximity.core.designsystem.foundation.label
import id.renaldi.proximity.core.designsystem.molecule.AppCard
import id.renaldi.proximity.core.designsystem.molecule.SectionHeader
import id.renaldi.proximity.core.designsystem.molecule.StatRow
import id.renaldi.proximity.core.model.TrackedDevice

@Composable
fun SignalStatsCard(tracked: TrackedDevice, modifier: Modifier = Modifier) = AppCard(modifier) {
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
        SectionHeader(title = Strings.TRACK_STABILITY)
        StatRow(label = Strings.TRACK_RAW_RSSI, value = "${tracked.device.rawRssi} dBm")
        StatRow(label = Strings.TRACK_SMOOTHED_RSSI, value = "${tracked.device.smoothedRssi} dBm")
        StatRow(
            label = Strings.TRACK_RANGE,
            value = DistanceFormat.formatRange(tracked.device.proximity.zone),
        )
        StatRow(
            label = Strings.TRACK_PACKET_RATE,
            value = tracked.stability.packetsPerSecond.toFixed(1),
        )
        StatRow(label = Strings.TRACK_JITTER, value = "${tracked.stability.jitterDb.toFixed(1)} dB")
        StatRow(
            label = Strings.TRACK_LAST_PACKET,
            value = RelativeTimeFormat.formatDuration(tracked.stability.millisSinceLastPacket),
        )
        StatRow(
            label = Strings.TRACK_STABILITY,
            value = tracked.stability.grade.label,
            valueColor = tracked.stability.grade.accentColor,
        )
    }
}

@Composable
fun DeviceFactsCard(tracked: TrackedDevice, modifier: Modifier = Modifier) = AppCard(modifier) {
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
        SectionHeader(title = Strings.TRACK_TITLE)
        StatRow(label = Strings.TRACK_TOTAL_PACKETS, value = tracked.device.packetCount.toString())
        StatRow(
            label = Strings.TRACK_CONNECTABLE,
            value = if (tracked.device.isConnectable) Strings.YES else Strings.NO,
        )
        StatRow(
            label = Strings.TRACK_TX_POWER,
            value = tracked.device.txPower?.let { "$it dBm" } ?: DistanceFormat.UNKNOWN,
        )
        StatRow(
            label = Strings.STRONGEST,
            value = "${tracked.strongestRssi} dBm",
            valueColor = tracked.device.proximity.zone.accentColor,
        )
    }
}
