package com.katudf.notgoodatmath.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = IndigoPrimary,
    onPrimary = Color.White,
    primaryContainer = IndigoLight,
    onPrimaryContainer = IndigoDark,
    secondary = Mint,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB8F2E6),
    onSecondaryContainer = MintDark,
    tertiary = Amber,
    background = Color(0xFFF6F7FB),
    onBackground = Color(0xFF1A1B22),
    surface = Color.White,
    onSurface = Color(0xFF1A1B22),
    surfaceVariant = Color(0xFFE8EAF2),
    onSurfaceVariant = Color(0xFF45474F),
    error = WrongRed,
)

private val DarkColors = darkColorScheme(
    primary = IndigoLight,
    onPrimary = IndigoDark,
    primaryContainer = IndigoDark,
    onPrimaryContainer = IndigoLight,
    secondary = Mint,
    onSecondary = Color(0xFF00382B),
    tertiary = Amber,
    background = Color(0xFF12131A),
    onBackground = Color(0xFFEDEEF5),
    surface = Color(0xFF1B1C24),
    onSurface = Color(0xFFEDEEF5),
    surfaceVariant = Color(0xFF2A2C36),
    onSurfaceVariant = Color(0xFFC3C5D0),
    error = WrongRed,
)

@Composable
fun NotGoodAtMathTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content,
    )
}
