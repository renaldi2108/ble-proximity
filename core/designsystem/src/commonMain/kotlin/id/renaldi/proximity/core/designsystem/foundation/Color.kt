package id.renaldi.proximity.core.designsystem.foundation

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

private val Teal200 = Color(0xFF6FE3D4)
private val Teal400 = Color(0xFF17B8A6)
private val Teal700 = Color(0xFF00695F)
private val Slate900 = Color(0xFF0B1116)
private val Slate850 = Color(0xFF111A21)
private val Slate800 = Color(0xFF17232C)
private val Slate700 = Color(0xFF223240)
private val Slate300 = Color(0xFF9FB1BD)
private val Slate100 = Color(0xFFE3EBF0)
private val Mist50 = Color(0xFFF5F8FA)
private val Mist100 = Color(0xFFE8EFF3)

internal val SignalImmediate = Color(0xFF2FD47A)
internal val SignalNear = Color(0xFF6FD943)
internal val SignalGood = Color(0xFFD8C53B)
internal val SignalWeak = Color(0xFFE8963C)
internal val SignalVeryWeak = Color(0xFFDE5B4C)
internal val SignalLost = Color(0xFF7E8C98)

val AppDarkColors = darkColorScheme(
    primary = Teal200,
    onPrimary = Color(0xFF00322C),
    primaryContainer = Teal700,
    onPrimaryContainer = Teal200,
    secondary = Slate300,
    onSecondary = Slate900,
    background = Slate900,
    onBackground = Slate100,
    surface = Slate850,
    onSurface = Slate100,
    surfaceVariant = Slate800,
    onSurfaceVariant = Slate300,
    surfaceContainer = Slate800,
    surfaceContainerLow = Slate850,
    surfaceContainerHigh = Slate700,
    outline = Slate700,
    outlineVariant = Color(0xFF1C2A34),
    error = SignalVeryWeak,
    onError = Color(0xFF3A0B07),
)

val AppLightColors = lightColorScheme(
    primary = Teal400,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBFF3EC),
    onPrimaryContainer = Teal700,
    secondary = Color(0xFF4C636E),
    onSecondary = Color.White,
    background = Mist50,
    onBackground = Color(0xFF11181D),
    surface = Color.White,
    onSurface = Color(0xFF11181D),
    surfaceVariant = Mist100,
    onSurfaceVariant = Color(0xFF4C636E),
    surfaceContainer = Mist100,
    surfaceContainerLow = Mist50,
    surfaceContainerHigh = Color(0xFFDCE6EB),
    outline = Color(0xFFB7C5CD),
    outlineVariant = Color(0xFFD7E2E8),
    error = Color(0xFFBE3025),
    onError = Color.White,
)
