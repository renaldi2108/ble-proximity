package id.renaldi.proximity.feature.tracker.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import id.renaldi.proximity.core.designsystem.preview.PreviewScreen
import id.renaldi.proximity.core.designsystem.preview.PreviewSurface
import id.renaldi.proximity.core.fixtures.PreviewSamples
import id.renaldi.proximity.core.model.BleFailure
import id.renaldi.proximity.core.model.SignalStability
import id.renaldi.proximity.feature.tracker.TrackerScreen
import id.renaldi.proximity.feature.tracker.TrackerUiState
import id.renaldi.proximity.feature.tracker.component.DeviceFactsCard
import id.renaldi.proximity.feature.tracker.component.SignalStatsCard

@Preview(name = "Tracker · menunggu sinyal pertama", widthDp = 393, heightDp = 800)
@Composable
private fun TrackerAcquiringPreview() = TrackerPreview(
    TrackerUiState(address = PreviewSamples.buds.address, isScanning = true),
)

@Preview(name = "Tracker · sangat dekat", widthDp = 393, heightDp = 1200)
@Composable
private fun TrackerImmediatePreview() = TrackerPreview(
    TrackerUiState(
        address = PreviewSamples.mouse.address,
        tracked = PreviewSamples.tracked(PreviewSamples.mouse),
        isScanning = true,
    ),
)

@Preview(name = "Tracker · mendekat", widthDp = 393, heightDp = 1200)
@Composable
private fun TrackerApproachingPreview() = TrackerPreview(
    TrackerUiState(
        address = PreviewSamples.buds.address,
        tracked = PreviewSamples.trackedNear,
        isScanning = true,
    ),
)

@Preview(name = "Tracker · menjauh", widthDp = 393, heightDp = 1200)
@Composable
private fun TrackerRecedingPreview() = TrackerPreview(
    TrackerUiState(
        address = PreviewSamples.band.address,
        tracked = PreviewSamples.trackedWeak,
        isScanning = true,
    ),
)

@Preview(name = "Tracker · sinyal hilang", widthDp = 393, heightDp = 1200)
@Composable
private fun TrackerLostPreview() = TrackerPreview(
    TrackerUiState(
        address = PreviewSamples.lost.address,
        tracked = PreviewSamples.trackedLost,
        isScanning = true,
    ),
)

@Preview(name = "Tracker · gelap", widthDp = 393, heightDp = 1200)
@Composable
private fun TrackerDarkPreview() = TrackerPreview(
    state = TrackerUiState(
        address = PreviewSamples.speaker.address,
        tracked = PreviewSamples.trackedGood,
        isScanning = true,
    ),
    darkTheme = true,
)

@Preview(name = "Tracker · bluetooth mati", widthDp = 393, heightDp = 1200)
@Composable
private fun TrackerBlockedPreview() = TrackerPreview(
    TrackerUiState(
        address = PreviewSamples.speaker.address,
        tracked = PreviewSamples.trackedGood,
        readinessFailure = BleFailure.BluetoothOff,
    ),
)

@Preview(name = "Tracker · komponen kartu statistik", widthDp = 393)
@Composable
private fun TrackerCardsPreview() = PreviewSurface {
    SignalStatsCard(tracked = PreviewSamples.trackedNear, modifier = Modifier)
    DeviceFactsCard(tracked = PreviewSamples.trackedNear, modifier = Modifier)
}

@Preview(name = "Tracker · komponen kartu tidak stabil", widthDp = 393)
@Composable
private fun TrackerUnstableCardPreview() = PreviewSurface(darkTheme = true) {
    SignalStatsCard(
        tracked = PreviewSamples.tracked(
            device = PreviewSamples.faraway,
            grade = SignalStability.Grade.UNSTABLE,
        ),
        modifier = Modifier,
    )
}

@Composable
private fun TrackerPreview(
    state: TrackerUiState,
    darkTheme: Boolean = false,
) = PreviewScreen(darkTheme = darkTheme) {
    TrackerScreen(
        state = state,
        onAction = {},
        onNavigate = {},
        onEnableBluetooth = {},
        onRequestPermission = {},
        onOpenLocationSettings = {},
    )
}
