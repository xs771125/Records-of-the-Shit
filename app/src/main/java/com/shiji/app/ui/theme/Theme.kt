package com.shiji.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = BambooAccent,
    onPrimary = ParchmentLight,
    primaryContainer = BambooMedium,
    onPrimaryContainer = BambooText,
    secondary = SealRed,
    onSecondary = ParchmentLight,
    secondaryContainer = ParchmentDark,
    onSecondaryContainer = InkBlack,
    tertiary = GoldAccent,
    onTertiary = ParchmentLight,
    background = ParchmentLight,
    onBackground = InkBlack,
    surface = ParchmentMedium,
    onSurface = InkBlack,
    surfaceVariant = BambooLight,
    onSurfaceVariant = InkGray,
    outline = BambooDark
)

private val DarkColorScheme = darkColorScheme(
    primary = BambooMedium,
    onPrimary = InkBlack,
    primaryContainer = BambooDark,
    onPrimaryContainer = ParchmentLight,
    secondary = SealRed,
    onSecondary = ParchmentLight,
    secondaryContainer = ParchmentDark,
    onSecondaryContainer = InkBlack,
    tertiary = GoldAccent,
    onTertiary = InkBlack,
    background = InkBlack,
    onBackground = ParchmentLight,
    surface = Color(0x3D, 0x2E, 0x1F),
    onSurface = ParchmentLight,
    surfaceVariant = Color(0x4A, 0x37, 0x28),
    onSurfaceVariant = ParchmentMedium,
    outline = BambooMedium
)

@Composable
fun ShijiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ShijiTypography,
        content = content
    )
}
