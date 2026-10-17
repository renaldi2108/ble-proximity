package id.renaldi.proximity.core.designsystem.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import id.renaldi.proximity.core.designsystem.foundation.AppDimens
import id.renaldi.proximity.core.designsystem.foundation.AppSpacing
import id.renaldi.proximity.core.designsystem.foundation.ProximityTheme

@Composable
fun PreviewSurface(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) = ProximityTheme(darkTheme = darkTheme) {
    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimens.pageMargin),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.lg),
            content = { content() },
        )
    }
}

@Composable
fun PreviewScreen(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) = ProximityTheme(darkTheme = darkTheme) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        content()
    }
}
