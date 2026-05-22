package com.alexbyr.game2048.ui.design

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GlassPanel(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        color = Color.Transparent,
        shape = RoundedCornerShape(28.dp),
        shadowElevation = 18.dp,
        border = BorderStroke(1.dp, GameTokens.boardStroke),
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            GameTokens.boardGlassTop,
                            GameTokens.boardGlassBottom,
                        ),
                    ),
                )
                .border(1.dp, GameTokens.boardInnerStroke, RoundedCornerShape(28.dp)),
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.28f),
                                Color.Transparent,
                                Color.White.copy(alpha = 0.08f),
                            ),
                        ),
                    ),
            )
            Box(modifier = Modifier.padding(0.dp)) {
                content()
            }
        }
    }
}

fun Modifier.softGlow(accent: Color): Modifier =
    this
        .clip(RoundedCornerShape(28.dp))
        .background(
            brush = Brush.radialGradient(
                colors = listOf(
                    accent.copy(alpha = 0.18f),
                    Color.Transparent,
                ),
            ),
        )
        .border(1.dp, GameTokens.boardStroke, RoundedCornerShape(28.dp))
