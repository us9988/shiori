package com.usnine.shiori.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary                = primary,
    secondary              = secondary,
    tertiary               = tertiary,
    background             = background,
    surface                = surface,
    surfaceContainerLowest = surfaceContainerLowest,
    outline                = outline,
    error                  = error,
    onPrimary              = onPrimary,
    onSecondary            = onSecondary,
    onTertiary             = onTertiary,
    onBackground           = onBackground,
    onSurface              = onSurface,
    onSurfaceVariant       = onSurfaceVariant,
    onError                = onError,
)

private val DarkColorScheme = darkColorScheme(
    primary                = primaryDark,
    secondary              = secondaryDark,
    tertiary               = tertiaryDark,
    background             = backgroundDark,
    surface                = surfaceDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    outline                = outlineDark,
    error                  = errorDark,
    onPrimary              = onPrimaryDark,
    onSecondary            = onSecondaryDark,
    onTertiary             = onTertiaryDark,
    onBackground           = onBackgroundDark,
    onSurface              = onSurfaceDark,
    onSurfaceVariant       = onSurfaceVariantDark,
    onError                = onErrorDark,
)

@Composable
fun ShioriTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        shapes      = Shapes,
        content     = content,
    )
}
