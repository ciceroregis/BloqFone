package br.com.bloqfone.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BlockBlue80,
    onPrimary = BlockBlueContainerDark,
    primaryContainer = BlockBlueContainerDark,
    onPrimaryContainer = BlockBlueContainerLight,
    secondary = BlockTeal80,
    secondaryContainer = BlockTealContainerDark,
    onSecondaryContainer = BlockTealContainerLight,
    tertiary = CalmSlate80,
    background = AppBackgroundDark,
    surface = AppBackgroundDark
)

private val LightColorScheme = lightColorScheme(
    primary = BlockBlue40,
    onPrimary = AppBackgroundLight,
    primaryContainer = BlockBlueContainerLight,
    onPrimaryContainer = BlockBlueContainerDark,
    secondary = BlockTeal40,
    secondaryContainer = BlockTealContainerLight,
    onSecondaryContainer = BlockTealContainerDark,
    tertiary = CalmSlate40,
    background = AppBackgroundLight,
    surface = AppBackgroundLight
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