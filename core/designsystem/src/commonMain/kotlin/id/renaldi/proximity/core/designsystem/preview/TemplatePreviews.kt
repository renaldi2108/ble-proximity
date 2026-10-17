package id.renaldi.proximity.core.designsystem.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import id.renaldi.proximity.core.designsystem.foundation.AppDimens
import id.renaldi.proximity.core.designsystem.foundation.Strings
import id.renaldi.proximity.core.designsystem.molecule.AppCard
import id.renaldi.proximity.core.designsystem.molecule.SectionHeader
import id.renaldi.proximity.core.designsystem.molecule.StatRow
import id.renaldi.proximity.core.designsystem.organism.ScanToggleButton
import id.renaldi.proximity.core.designsystem.template.AppScaffold

@Preview(name = "Template · scaffold", widthDp = 393, heightDp = 640)
@Composable
private fun AppScaffoldPreview() = PreviewScreen {
    AppScaffold(
        title = Strings.APP_NAME,
        subtitle = "7 ${Strings.DEVICES_FOUND}",
        actions = {
            IconButton(onClick = {}) {
                Icon(Icons.Filled.History, contentDescription = Strings.HISTORY_TITLE)
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(AppDimens.pageMargin),
        ) {
            ScanToggleButton(isScanning = true, enabled = true, onToggle = {})
        }
    }
}

@Preview(name = "Template · scaffold dengan tombol kembali", widthDp = 393, heightDp = 640)
@Composable
private fun AppScaffoldDetailPreview() = PreviewScreen(darkTheme = true) {
    AppScaffold(
        title = "Galaxy Buds Pro",
        subtitle = "C4:1A:9E:72:00:11",
        onBack = {},
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(AppDimens.pageMargin),
        ) {
            AppCard(modifier = Modifier.fillMaxWidth()) {
                SectionHeader(title = Strings.TRACK_STABILITY)
                StatRow(label = Strings.TRACK_RAW_RSSI, value = "-59 dBm")
                StatRow(label = Strings.TRACK_PACKET_RATE, value = "8,4")
            }
        }
    }
}
