package id.renaldi.proximity.feature.history

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import id.renaldi.proximity.core.common.format.RelativeTimeFormat
import id.renaldi.proximity.core.designsystem.atom.AppText
import id.renaldi.proximity.core.designsystem.atom.LoadingSkeleton
import id.renaldi.proximity.core.designsystem.foundation.AppSpacing
import id.renaldi.proximity.core.designsystem.foundation.Strings
import id.renaldi.proximity.core.designsystem.organism.DeviceRow
import id.renaldi.proximity.core.designsystem.organism.EmptyState
import id.renaldi.proximity.core.designsystem.template.AppScaffold
import id.renaldi.proximity.core.model.DeviceHistoryEntry

@Composable
fun HistoryScreen(
    state: HistoryUiState,
    onAction: (HistoryAction) -> Unit,
    onNavigate: (HistoryNavigation) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) = AppScaffold(
    title = Strings.HISTORY_TITLE,
    subtitle = "${state.count} ${Strings.DEVICES_FOUND}",
    modifier = modifier,
    onBack = { onNavigate(HistoryNavigation.Back) },
    snackbarHostState = snackbarHostState,
    actions = {
        if (state.canClear) {
            IconButton(onClick = { onAction(HistoryAction.RequestClear) }) {
                Icon(
                    imageVector = Icons.Filled.DeleteSweep,
                    contentDescription = Strings.HISTORY_CLEAR,
                )
            }
        }
    },
) { innerPadding ->
    when (val content = state.content) {
        HistoryContent.Loading -> LoadingSkeleton(
            modifier = Modifier.fillMaxWidth().padding(innerPadding),
        )

        HistoryContent.Empty -> EmptyState(
            icon = Icons.Filled.History,
            title = Strings.HISTORY_EMPTY_TITLE,
            body = Strings.HISTORY_EMPTY_BODY,
            modifier = Modifier.padding(innerPadding),
        )

        is HistoryContent.Entries -> LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding() + AppSpacing.xxl,
            ),
        ) {
            items(
                items = content.entries,
                key = DeviceHistoryEntry::address,
                contentType = { HISTORY_ROW },
            ) { entry ->
                DeviceRow(
                    title = entry.displayName,
                    address = entry.address,
                    zone = entry.lastZone,
                    primaryMetric = "${entry.lastRssi} dBm",
                    secondaryMetric = RelativeTimeFormat.format(
                        timestampMillis = entry.lastSeenAtMillis,
                        nowMillis = state.nowMillis,
                    ),
                    onClick = { onNavigate(HistoryNavigation.OpenTracker(entry.address)) },
                    trailing = {
                        IconButton(onClick = { onAction(HistoryAction.Delete(entry.address)) }) {
                            Icon(
                                imageVector = Icons.Filled.DeleteOutline,
                                contentDescription = Strings.HISTORY_DELETE,
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }

    if (state.isClearDialogOpen) {
        AlertDialog(
            onDismissRequest = { onAction(HistoryAction.DismissClear) },
            title = { AppText(text = Strings.HISTORY_CLEAR_CONFIRM) },
            confirmButton = {
                TextButton(onClick = { onAction(HistoryAction.ConfirmClear) }) {
                    AppText(text = Strings.HISTORY_DELETE)
                }
            },
            dismissButton = {
                TextButton(onClick = { onAction(HistoryAction.DismissClear) }) {
                    AppText(text = Strings.HISTORY_CANCEL)
                }
            },
        )
    }
}

private const val HISTORY_ROW = "historyRow"
