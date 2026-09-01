package studio.cluvex.aether.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val PsitherModernColorScheme = darkColorScheme(
    primary = AetherBlue,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF0C4A6E),
    onPrimaryContainer = Color(0xFFE0F2FE),
    secondary = AetherCyan,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF4C1D95),
    onSecondaryContainer = Color(0xFFF3E8FF),
    tertiary = AetherMint,
    onTertiary = Color.Black,
    background = Navy900,
    onBackground = OnDark,
    surface = Navy800,
    onSurface = OnDark,
    surfaceVariant = Navy700,
    onSurfaceVariant = OnDarkMuted,
    error = AetherError,
    onError = Color.White,
    outline = Navy600,
    outlineVariant = Color(0xFF1E293B),
)

/**
 * Modern Psither Theme with vibrant ambient glow and dark slate surfaces.
 */
@Composable
fun AetherTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = PsitherModernColorScheme,
        typography = AetherTypography,
        content = content,
    )
}
