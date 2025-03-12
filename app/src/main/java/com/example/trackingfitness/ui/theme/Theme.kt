package com.example.trackingfitness.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color.White,
    background = OnBackgroundColor,
    surface = Color.Black,
    secondary = OnBoxColor,
    tertiary = OnBorderColor,
    onSecondary = OnBorderColor,
    onSecondaryContainer = PositionColor
)

private val LightColorScheme = lightColorScheme(
    primary = TextColor,
    background = BackgroundColor,
    secondary = BoxColor,
    tertiary = BorderColor,
    onSecondary = RankingColor,
    onSecondaryContainer = PositionColor
)

@Composable
fun TrackingFitnessTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    forceDarkTheme: Boolean? = null,
    content: @Composable () -> Unit
) {
    val colorScheme = when
    {
        forceDarkTheme == true -> DarkColorScheme
        forceDarkTheme == false -> LightColorScheme
        else -> if (darkTheme) DarkColorScheme else LightColorScheme
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes(),
        content = content
    )
}