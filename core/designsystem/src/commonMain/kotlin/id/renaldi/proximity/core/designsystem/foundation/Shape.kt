package id.renaldi.proximity.core.designsystem.foundation

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

object AppShape {
    val card = RoundedCornerShape(16.dp)
    val field = RoundedCornerShape(12.dp)
    val chip = RoundedCornerShape(10.dp)
    val pill = CircleShape
    val sheet = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
}
