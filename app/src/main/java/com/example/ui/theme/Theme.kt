package com.example.ui.theme

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

private val DarkColorScheme =
  darkColorScheme(
    primary = LuxeGoldPrimary,
    onPrimary = LuxeGoldOnPrimary,
    primaryContainer = LuxeGoldContainer,
    onPrimaryContainer = LuxeOnGoldContainer,
    secondary = LuxeBronzeSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF2E2412),
    onSecondaryContainer = Color(0xFFF7E6A8),
    tertiary = LuxeVioletTertiary,
    onTertiary = Color.White,
    background = LuxeDarkBackground,
    onBackground = LuxeTextPrimary,
    surface = LuxeDarkSurface,
    onSurface = LuxeTextPrimary,
    surfaceVariant = LuxeDarkSurfaceVariant,
    onSurfaceVariant = LuxeTextSecondary,
    outline = LuxeOutline,
    outlineVariant = Color(0xFF222B3D)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = LuxeNavyPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE2E8F0),
    onPrimaryContainer = LuxeNavyPrimary,
    secondary = LuxeBronzeSecondary,
    onSecondary = Color.White,
    tertiary = LuxeVioletTertiary,
    onTertiary = Color.White,
    background = LuxeLightBackground,
    surface = LuxeLightSurface,
    surfaceVariant = LuxeLightSurfaceVariant,
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1)
  )

@Composable
fun ThreeBrothersTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  ThreeBrothersTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}

