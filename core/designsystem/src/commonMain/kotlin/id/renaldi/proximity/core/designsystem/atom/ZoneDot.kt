package id.renaldi.proximity.core.designsystem.atom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import id.renaldi.proximity.core.designsystem.foundation.AppDimens
import id.renaldi.proximity.core.designsystem.foundation.AppShape
import id.renaldi.proximity.core.designsystem.foundation.accentColor
import id.renaldi.proximity.core.model.ProximityZone

@Composable
fun ZoneDot(
    zone: ProximityZone,
    modifier: Modifier = Modifier,
    size: Dp = AppDimens.zoneDot,
) = Box(
    modifier = modifier
        .size(size)
        .background(color = zone.accentColor, shape = AppShape.pill),
)
