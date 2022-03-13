package dev.logickoder.currencycalculator.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ColorPalette = lightColors(
    primary = ColorPrimary,
    primaryVariant = ColorPrimaryLight,
    secondary = ColorSecondary,
    onPrimary = Color.White,
    onSecondary = Color.White,
    surface = ColorSurface,
    onSurface = ColorOnSurface,

    /* Other default colors to override
    background = Color.White,
    onBackground = Color.Black,
    */
)

@Composable
fun CurrencyCalculatorTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = ColorPalette,
//        typography = Typography,
        shapes = Shapes,
        content = content
    )
}