package id.renaldi.proximity.feature.history.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import id.renaldi.proximity.core.designsystem.preview.PreviewScreen
import id.renaldi.proximity.core.fixtures.PreviewSamples
import id.renaldi.proximity.feature.history.HistoryContent
import id.renaldi.proximity.feature.history.HistoryScreen
import id.renaldi.proximity.feature.history.HistoryUiState

@Preview(name = "Riwayat · memuat", widthDp = 393, heightDp = 800)
@Composable
private fun HistoryLoadingPreview() = HistoryPreview(
    HistoryUiState(nowMillis = PreviewSamples.NOW_MILLIS),
)

@Preview(name = "Riwayat · kosong", widthDp = 393, heightDp = 800)
@Composable
private fun HistoryEmptyPreview() = HistoryPreview(
    HistoryUiState(nowMillis = PreviewSamples.NOW_MILLIS, content = HistoryContent.Empty),
)

@Preview(name = "Riwayat · berisi", widthDp = 393, heightDp = 800)
@Composable
private fun HistoryEntriesPreview() = HistoryPreview(
    HistoryUiState(
        nowMillis = PreviewSamples.NOW_MILLIS,
        content = HistoryContent.Entries(PreviewSamples.historyEntries),
    ),
)

@Preview(name = "Riwayat · berisi gelap", widthDp = 393, heightDp = 800)
@Composable
private fun HistoryEntriesDarkPreview() = HistoryPreview(
    state = HistoryUiState(
        nowMillis = PreviewSamples.NOW_MILLIS,
        content = HistoryContent.Entries(PreviewSamples.historyEntries),
    ),
    darkTheme = true,
)

@Preview(name = "Riwayat · konfirmasi hapus semua", widthDp = 393, heightDp = 800)
@Composable
private fun HistoryClearDialogPreview() = HistoryPreview(
    HistoryUiState(
        nowMillis = PreviewSamples.NOW_MILLIS,
        content = HistoryContent.Entries(PreviewSamples.historyEntries),
        isClearDialogOpen = true,
    ),
)

@Composable
private fun HistoryPreview(
    state: HistoryUiState,
    darkTheme: Boolean = false,
) = PreviewScreen(darkTheme = darkTheme) {
    HistoryScreen(state = state, onAction = {}, onNavigate = {})
}
