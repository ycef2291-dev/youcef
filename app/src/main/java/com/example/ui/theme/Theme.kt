package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ClickaColorScheme = darkColorScheme(
    primary = ClickaPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF381559),
    onPrimaryContainer = Color(0xFFEADBFF),
    secondary = ClickaSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF5E0B2B),
    onSecondaryContainer = Color(0xFFFFD8E4),
    tertiary = ClickaTertiary,
    onTertiary = Color.Black,
    background = ClickaDarkBg,
    onBackground = ClickaTextPrimary,
    surface = ClickaCardBg,
    onSurface = ClickaTextPrimary,
    surfaceVariant = ClickaCardElevated,
    onSurfaceVariant = ClickaTextSecondary,
    outline = ClickaDivider
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our signature Clicka streaming theme
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ClickaColorScheme,
        typography = Typography,
        content = content
    )
}
