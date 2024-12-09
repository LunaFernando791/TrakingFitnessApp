package com.example.trackingfitness.ui.theme


import android.os.Build
import android.os.Build.VERSION_CODES
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
    tertiary = OnBorderColor

)

private val LightColorScheme = lightColorScheme(
    primary = TextColor,
    background = BackgroundColor,
    secondary = BoxColor,
    tertiary = BorderColor,
)

@Composable
fun TrackingFitnessTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes(),
        content = content
    )
}