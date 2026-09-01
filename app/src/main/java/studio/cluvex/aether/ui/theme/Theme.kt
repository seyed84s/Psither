package studio.cluvex.aether.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Fallback scheme: a stunning navy dark theme for devices below Android 12.
private val PsitherDarkColorScheme = darkColorScheme(
    primary = AetherBlue,
    onPrimary = Color.Black, // Gold looks better with black text
    secondary = AetherCyan,
    onSecondary = Color.Black,
    tertiary = AetherCyan,
    background = Navy900,
    onBackground = OnDark,
    surface = Navy800,
    onSurface = OnDark,
    surfaceVariant = Navy700,
    onSurfaceVariant = OnDarkMuted,
    error = AetherError,
    onError = Color.White,
    outline = Navy600,
)

/**
 * Custom Stealth Elite theme. Always dark by design. Disables dynamic colors to enforce branding.
 */
@Composable
fun AetherTheme(content: @Composable () -> Unit) {
    val colorScheme = PsitherDarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AetherTypography,
        content = content,
    )
}
