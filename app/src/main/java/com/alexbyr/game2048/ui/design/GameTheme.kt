package com.alexbyr.game2048.ui.design

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val GameColorScheme = lightColorScheme(
    primary = Color(0xFF5568FF),
    secondary = Color(0xFFFFA47A),
    tertiary = Color(0xFFFFE08F),
    background = Color(0xFFF6F3FB),
    surface = Color(0xFFFDFBFF),
    onSurface = Color(0xFF221B2D),
    onBackground = Color(0xFF221B2D),
)

private val GameTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 40.sp,
        lineHeight = 42.sp,
        letterSpacing = (-1).sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.5).sp,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        letterSpacing = 0.2.sp,
    ),
)

@Composable
fun Game2048Theme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = GameColorScheme,
        typography = GameTypography,
        content = content,
    )
}
