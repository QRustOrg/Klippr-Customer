package com.example.klippr.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = KlipprPurple,
    onPrimary = Color.White,
    primaryContainer = KlipprLavender,
    secondary = PurpleGrey80,
    tertiary = Pink80,
)

private val LightColorScheme = lightColorScheme(
    primary = KlipprPurple,
    onPrimary = Color.White,
    // primaryContainer se usa para indicadores de navegación, chips seleccionados, etc.
    primaryContainer = KlipprLavender,
    onPrimaryContainer = Color(0xFF21005D),
    secondary = KlipprPurpleDark,
    onSecondary = Color.White,
    background = Color.White,
    surface = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFF3F3F3),
)

@Composable
fun KlipprTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // dynamicColor deshabilitado para preservar la identidad visual de Klippr en Android 12+.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}