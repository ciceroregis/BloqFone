package br.com.bloqfone.ui.theme

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
    primary = ShieldBlueDark,
    onPrimary = Color(0xFF0F172A),
    primaryContainer = ShieldBlueContainerDark,
    onPrimaryContainer = ShieldBlueContainerLight,
    secondary = ShieldTealDark,
    secondaryContainer = ShieldTealContainerDark,
    onSecondaryContainer = ShieldTealContainerLight,
    background = AppBackgroundDark,
    onBackground = Color(0xFFF8FAFC),
    surface = AppSurfaceDark,
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFFCBD5E1)
)

private val LightColorScheme = lightColorScheme(
    primary = ShieldBlueLight,
    onPrimary = Color.White,
    primaryContainer = ShieldBlueContainerLight,
    onPrimaryContainer = ShieldBlueLight,
    secondary = ShieldTealLight,
    secondaryContainer = ShieldTealContainerLight,
    onSecondaryContainer = ShieldTealLight,
    background = AppBackgroundLight,
    onBackground = Color(0xFF0F172A),
    surface = AppSurfaceLight,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF334155)
)

@Composable
fun BloqFoneTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
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