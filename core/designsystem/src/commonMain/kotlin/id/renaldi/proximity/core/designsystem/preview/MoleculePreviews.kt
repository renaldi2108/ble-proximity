package id.renaldi.proximity.core.designsystem.preview

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import id.renaldi.proximity.core.designsystem.atom.AppText
import id.renaldi.proximity.core.designsystem.foundation.Strings
import id.renaldi.proximity.core.designsystem.foundation.accentColor
import id.renaldi.proximity.core.designsystem.molecule.AppCard
import id.renaldi.proximity.core.designsystem.molecule.LabeledValue
import id.renaldi.proximity.core.designsystem.molecule.NoticeBanner
import id.renaldi.proximity.core.designsystem.molecule.SearchField
import id.renaldi.proximity.core.designsystem.molecule.SectionHeader
import id.renaldi.proximity.core.designsystem.molecule.StatRow
import id.renaldi.proximity.core.designsystem.molecule.ThresholdSlider
import id.renaldi.proximity.core.model.DeviceFilter
import id.renaldi.proximity.core.model.ProximityZone

@Preview(name = "Molekul · kartu", widthDp = 393)
@Composable
private fun AppCardPreview() = PreviewSurface {
    AppCard {
        SectionHeader(title = Strings.TRACK_STABILITY)
        StatRow(label = Strings.TRACK_RAW_RSSI, value = "-59 dBm")
        StatRow(label = Strings.TRACK_SMOOTHED_RSSI, value = "-57 dBm")
        StatRow(
            label = Strings.TRACK_STABILITY,
            value = "Stabil",
            valueColor = ProximityZone.NEAR.accentColor,
        )
    }
    AppCard(onClick = {}) {
        AppText(text = "Kartu yang bisa ditekan", style = MaterialTheme.typography.titleMedium)
    }
}

@Preview(name = "Molekul · kartu gelap", widthDp = 393)
@Composable
private fun AppCardDarkPreview() = PreviewSurface(darkTheme = true) {
    AppCard {
        SectionHeader(title = Strings.TRACK_STABILITY)
        StatRow(label = Strings.TRACK_PACKET_RATE, value = "8,4")
        StatRow(label = Strings.TRACK_JITTER, value = "1,6 dB")
    }
}

@Preview(name = "Molekul · kolom pencarian", widthDp = 393)
@Composable
private fun SearchFieldPreview() = PreviewSurface {
    var empty by remember { mutableStateOf("") }
    var filled by remember { mutableStateOf("JBL") }
    SearchField(value = empty, onValueChange = { empty = it }, placeholder = Strings.SEARCH_PLACEHOLDER)
    SearchField(value = filled, onValueChange = { filled = it }, placeholder = Strings.SEARCH_PLACEHOLDER)
}

@Preview(name = "Molekul · nilai berlabel", widthDp = 393)
@Composable
private fun LabeledValuePreview() = PreviewSurface {
    LabeledValue(label = Strings.TRACK_DISTANCE, value = "3,0 m")
    LabeledValue(
        label = Strings.TRACK_RANGE,
        value = "3 – 10 m",
        valueColor = ProximityZone.GOOD.accentColor,
    )
    StatRow(label = Strings.TRACK_TOTAL_PACKETS, value = "734")
}

@Preview(name = "Molekul · judul bagian", widthDp = 393)
@Composable
private fun SectionHeaderPreview() = PreviewSurface {
    SectionHeader(title = Strings.TRACK_HISTORY)
    SectionHeader(
        title = Strings.FILTER_SORT,
        trailing = { AppText(text = Strings.FILTER_RESET, style = MaterialTheme.typography.labelLarge) },
    )
}

@Preview(name = "Molekul · banner", widthDp = 393)
@Composable
private fun NoticeBannerPreview() = PreviewSurface {
    NoticeBanner(
        title = Strings.BLUETOOTH_OFF_TITLE,
        body = Strings.BLUETOOTH_OFF_BODY,
        icon = Icons.Filled.BluetoothDisabled,
        actionLabel = Strings.BLUETOOTH_OFF_ACTION,
        onAction = {},
    )
    NoticeBanner(
        title = Strings.PERMISSION_TITLE,
        body = Strings.PERMISSION_BODY,
        icon = Icons.Filled.Lock,
        accent = MaterialTheme.colorScheme.primary,
    )
}

@Preview(name = "Molekul · slider ambang", widthDp = 393)
@Composable
private fun ThresholdSliderPreview() = PreviewSurface {
    var threshold by remember { mutableIntStateOf(-70) }
    ThresholdSlider(
        label = Strings.FILTER_THRESHOLD,
        valueLabel = "≥ $threshold dBm",
        value = threshold,
        range = DeviceFilter.RSSI_THRESHOLD_RANGE,
        onValueChange = { threshold = it },
        modifier = Modifier,
    )
}
