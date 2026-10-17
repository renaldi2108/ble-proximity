package id.renaldi.proximity.core.designsystem.molecule

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import id.renaldi.proximity.core.designsystem.foundation.AppDimens
import id.renaldi.proximity.core.designsystem.foundation.AppShape
import id.renaldi.proximity.core.designsystem.foundation.AppSpacing

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val border = BorderStroke(AppDimens.hairline, MaterialTheme.colorScheme.outlineVariant)
    val body: @Composable () -> Unit = {
        Column(
            modifier = Modifier.padding(AppSpacing.lg),
            content = content,
        )
    }

    if (onClick == null) {
        Surface(
            modifier = modifier,
            shape = AppShape.card,
            color = MaterialTheme.colorScheme.surfaceContainer,
            border = border,
            content = body,
        )
    } else {
        Surface(
            modifier = modifier,
            onClick = onClick,
            shape = AppShape.card,
            color = MaterialTheme.colorScheme.surfaceContainer,
            border = border,
            content = body,
        )
    }
}
