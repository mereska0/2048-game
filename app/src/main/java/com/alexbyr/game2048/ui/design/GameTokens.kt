package com.alexbyr.game2048.ui.design

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object GameTokens {
    val pageGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFF9F7FF),
            Color(0xFFF2EDFF),
            Color(0xFFFFF0E5),
        ),
    )

    val boardGlassTop = Color(0xE8FFFFFF)
    val boardGlassBottom = Color(0xA8FFFFFF)
    val boardStroke = Color(0x80FFFFFF)
    val boardInnerStroke = Color(0x50FFFFFF)
    val shadow = Color(0x1F3F2A5E)
    val textMuted = Color(0xFF72667D)
    val hudTitle = Color(0xFF32273C)
    val emptyCell = Color(0x14FFFFFF)
    val overlay = Color(0x7A20142C)
    val boardFlash = Color(0x66FFFFFF)
    val tileStroke = Color(0x96FFFFFF)

    fun tileColor(value: Int): Color =
        when (value) {
            2 -> Color(0xCFFAF6FF)
            4 -> Color(0xD6FFF8F0)
            8 -> Color(0xDAFFF0D6)
            16 -> Color(0xDFFFE2C8)
            32 -> Color(0xE5FFD5C1)
            64 -> Color(0xE8FFC4C6)
            128 -> Color(0xDDF8CCFF)
            256 -> Color(0xD8E1D1FF)
            512 -> Color(0xD2C9D4FF)
            1024 -> Color(0xCCBAC8FF)
            2048 -> Color(0xC6A7BAFF)
            else -> Color(0xD05A5272)
        }

    fun tileTextColor(value: Int): Color =
        Color(0xFF2C2235)
}
