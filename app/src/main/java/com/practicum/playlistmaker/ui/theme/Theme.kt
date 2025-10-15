package com.practicum.playlistmaker.ui.theme

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
    primary = Color.Black,
    onPrimary = Blue,
    secondary = Color.White,
    tertiary = Dark,
    tertiaryContainer = Color.White,
    background = Dark,
    onBackground = Color.White,
    tertiaryFixed = DarkBlue,
    tertiaryFixedDim = Blue
)

private val LightColorScheme = lightColorScheme(
    primary = Color.Black,
    onPrimary = Blue,
    secondary = Gray,
    tertiary = Gray,
    tertiaryContainer = LightGray,
    background = Color.White,
    onBackground = Dark,
    tertiaryFixed = LightGray,
    tertiaryFixedDim = Gray
)

@Composable
fun PlaylistMakerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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
