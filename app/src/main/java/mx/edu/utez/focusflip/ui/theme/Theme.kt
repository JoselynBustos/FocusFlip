package mx.edu.utez.focusflip.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val FocusFlipDarkColorScheme = darkColorScheme(
    primary = NeonEmerald,
    onPrimary = Slate950,
    secondary = NeonBlue,
    onSecondary = Slate100,
    tertiary = NeonPurple,
    background = Slate950,
    onBackground = Slate100,
    surface = Slate900,
    onSurface = Slate100,
    surfaceVariant = Slate800,
    onSurfaceVariant = Slate400,
    error = OracleRed
)

@Composable
fun FocusFlipTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = FocusFlipDarkColorScheme,
        typography = Typography,
        content = content
    )
}
