package com.alexbyr.game2048.ui.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexbyr.game2048.R
import com.alexbyr.game2048.game.domain.BOARD_SIZE
import com.alexbyr.game2048.game.domain.GameStatus
import com.alexbyr.game2048.game.domain.MoveDirection
import com.alexbyr.game2048.game.presentation.GameTileUi
import com.alexbyr.game2048.game.presentation.GameUiState
import com.alexbyr.game2048.ui.design.Game2048Theme
import com.alexbyr.game2048.ui.design.GameTokens
import com.alexbyr.game2048.ui.design.GlassPanel
import com.alexbyr.game2048.ui.design.softGlow
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun GameScreen(
    state: GameUiState,
    onMove: (MoveDirection) -> Unit,
    onNewGame: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GameTokens.pageGradient)
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        BackgroundBlobs()

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            HeaderSection(
                score = state.score,
                bestScore = state.bestScore,
                onNewGame = onNewGame,
            )
            BoardSection(
                state = state,
                onMove = onMove,
                onNewGame = onNewGame,
                modifier = Modifier.weight(1f, fill = false),
            )
            FooterHint()
        }
    }
}

@Composable
private fun HeaderSection(
    score: Int,
    bestScore: Int,
    onNewGame: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = "2048",
                    style = MaterialTheme.typography.headlineLarge,
                    color = GameTokens.hudTitle,
                )
                Text(
                    text = "Modern tile puzzle with fluid motion and a clean board-first layout.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = GameTokens.textMuted,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            FilledTonalButton(onClick = onNewGame, shape = RoundedCornerShape(20.dp)) {
                Icon(Icons.Rounded.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.new_game))
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ScoreCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.score),
                value = score.toString(),
            )
            ScoreCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.best),
                value = bestScore.toString(),
            )
        }
    }
}

@Composable
private fun ScoreCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
) {
    GlassPanel(modifier = modifier.softGlow(MaterialTheme.colorScheme.primary)) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = GameTokens.textMuted,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = GameTokens.hudTitle,
            )
        }
    }
}

@Composable
private fun BoardSection(
    state: GameUiState,
    onMove: (MoveDirection) -> Unit,
    onNewGame: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
        ) {
            BoardInputLayer(
                state = state,
                cellGap = 12.dp,
                onMove = onMove,
            )

            StatusOverlay(
                status = state.status,
                onNewGame = onNewGame,
            )
        }
    }
}

@Composable
private fun BoardInputLayer(
    state: GameUiState,
    cellGap: androidx.compose.ui.unit.Dp,
    onMove: (MoveDirection) -> Unit,
) {
    val focusKeyMap = remember {
        mapOf(
            Key.DirectionLeft to MoveDirection.Left,
            Key.DirectionRight to MoveDirection.Right,
            Key.DirectionUp to MoveDirection.Up,
            Key.DirectionDown to MoveDirection.Down,
            Key.A to MoveDirection.Left,
            Key.D to MoveDirection.Right,
            Key.W to MoveDirection.Up,
            Key.S to MoveDirection.Down,
        )
    }
    val flashAlpha = remember { Animatable(0f) }

    LaunchedEffect(state.moveCount, state.largeMergePulse) {
        if (state.largeMergePulse) {
            flashAlpha.snapTo(0.42f)
            flashAlpha.animateTo(
                targetValue = 0f,
                animationSpec = spring(stiffness = Spring.StiffnessLow),
            )
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .shadow(28.dp, RoundedCornerShape(32.dp), ambientColor = GameTokens.shadow, spotColor = GameTokens.shadow)
            .clip(RoundedCornerShape(32.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color.White.copy(alpha = 0.62f),
                        Color.White.copy(alpha = 0.30f),
                    ),
                ),
            )
            .onPreviewKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                focusKeyMap[event.key]?.let(onMove) != null
            }
            .focusable()
            .pointerInput(Unit) {
                var dragX = 0f
                var dragY = 0f
                detectDragGestures(
                    onDragStart = {
                        dragX = 0f
                        dragY = 0f
                    },
                    onDragEnd = {
                        val threshold = minOf(size.width, size.height) * 0.12f
                        val horizontal = abs(dragX) > abs(dragY)
                        when {
                            abs(dragX) < threshold && abs(dragY) < threshold -> Unit
                            horizontal && dragX > 0f -> onMove(MoveDirection.Right)
                            horizontal -> onMove(MoveDirection.Left)
                            dragY > 0f -> onMove(MoveDirection.Down)
                            else -> onMove(MoveDirection.Up)
                        }
                    },
                ) { change, dragAmount ->
                    change.consume()
                    dragX += dragAmount.x
                    dragY += dragAmount.y
                }
            }
            .padding(12.dp),
    ) {
        val gapPx = with(LocalDensity.current) { cellGap.toPx() }
        val contentSizePx = constraints.maxWidth.toFloat()
        val cellSizePx = if (contentSizePx == 0f) 0f else (contentSizePx - gapPx * (BOARD_SIZE - 1)) / BOARD_SIZE

        BoardGrid(gap = cellGap)
        AnimatedTiles(
            tiles = state.tiles,
            cellSizePx = cellSizePx,
            gapPx = gapPx,
            moveCount = state.moveCount,
            spawnedTileId = state.spawnedTileId,
            mergedTileIds = state.mergedTileIds,
        )

        if (flashAlpha.value > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(GameTokens.boardFlash.copy(alpha = flashAlpha.value))
            )
        }
    }
}

@Composable
private fun BoardGrid(gap: androidx.compose.ui.unit.Dp) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(gap),
    ) {
        repeat(BOARD_SIZE) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(gap),
            ) {
                repeat(BOARD_SIZE) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        shape = RoundedCornerShape(22.dp),
                        color = GameTokens.emptyCell,
                    ) {}
                }
            }
        }
    }
}

@Composable
private fun AnimatedTiles(
    tiles: List<GameTileUi>,
    cellSizePx: Float,
    gapPx: Float,
    moveCount: Int,
    spawnedTileId: Int?,
    mergedTileIds: Set<Int>,
) {
    tiles.forEach { tile ->
        key(tile.id) {
            val animatedX by animateFloatAsState(
                targetValue = tile.col * (cellSizePx + gapPx),
                label = "tile-x-${tile.id}",
                animationSpec = tween(durationMillis = 150),
            )
            val animatedY by animateFloatAsState(
                targetValue = tile.row * (cellSizePx + gapPx),
                label = "tile-y-${tile.id}",
                animationSpec = tween(durationMillis = 150),
            )
            val pulseScale = remember(tile.id) { Animatable(1f) }
            val travelStretchX = remember(tile.id) { Animatable(1f) }
            val travelStretchY = remember(tile.id) { Animatable(1f) }
            val travelGlow = remember(tile.id) { Animatable(0f) }
            val shouldPulse = tile.id == spawnedTileId || tile.id in mergedTileIds
            val rowShift = tile.row - tile.previousRow
            val colShift = tile.col - tile.previousCol
            val movedThisTurn = rowShift != 0 || colShift != 0

            LaunchedEffect(moveCount, shouldPulse, movedThisTurn, rowShift, colShift) {
                if (movedThisTurn) {
                    travelGlow.snapTo(0.22f)
                    if (abs(colShift) >= abs(rowShift)) {
                        travelStretchX.snapTo(1.12f)
                        travelStretchY.snapTo(0.94f)
                    } else {
                        travelStretchX.snapTo(0.94f)
                        travelStretchY.snapTo(1.12f)
                    }
                    travelStretchX.animateTo(1f, animationSpec = tween(durationMillis = 170))
                    travelStretchY.animateTo(1f, animationSpec = tween(durationMillis = 170))
                    travelGlow.animateTo(0f, animationSpec = tween(durationMillis = 170))
                }
            }

            LaunchedEffect(moveCount, shouldPulse) {
                if (shouldPulse) {
                    pulseScale.snapTo(0.78f)
                    pulseScale.animateTo(
                        targetValue = 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow,
                        ),
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(with(LocalDensity.current) { cellSizePx.toDp() })
                    .offset {
                        IntOffset(animatedX.roundToInt(), animatedY.roundToInt())
                    }
                    .graphicsLayer {
                        scaleX = pulseScale.value * travelStretchX.value
                        scaleY = pulseScale.value * travelStretchY.value
                    },
                contentAlignment = Alignment.Center,
            ) {
                TileCard(
                    tile = tile,
                    motionGlowAlpha = travelGlow.value,
                )
            }
        }
    }
}

@Composable
private fun TileCard(
    tile: GameTileUi,
    motionGlowAlpha: Float,
) {
    val accent = GameTokens.tileColor(tile.value)
    val textColor = GameTokens.tileTextColor(tile.value)

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .shadow(14.dp, RoundedCornerShape(22.dp), ambientColor = accent.copy(alpha = 0.24f), spotColor = accent.copy(alpha = 0.18f))
            .border(1.dp, GameTokens.tileStroke, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        color = accent,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.58f + motionGlowAlpha),
                            Color.White.copy(alpha = 0.16f),
                            accent.copy(alpha = 0.06f),
                        ),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(alpha = 0.22f)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.28f),
                                Color.Transparent,
                                Color.White.copy(alpha = 0.12f),
                            ),
                        ),
                    ),
            )
            Text(
                text = tile.value.toString(),
                color = textColor,
                fontWeight = FontWeight.ExtraBold,
                fontSize = tileFontSize(tile.value),
            )
        }
    }
}

@Composable
private fun StatusOverlay(
    status: GameStatus,
    onNewGame: () -> Unit,
) {
    val visible = status != GameStatus.Active
    val title = when (status) {
        GameStatus.Won -> stringResource(R.string.win_title)
        GameStatus.Lost -> stringResource(R.string.lose_title)
        GameStatus.Active -> ""
    }
    val message = when (status) {
        GameStatus.Won -> stringResource(R.string.win_message)
        GameStatus.Lost -> stringResource(R.string.lose_message)
        GameStatus.Active -> ""
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(GameTokens.overlay),
            contentAlignment = Alignment.Center,
        ) {
            GlassPanel(modifier = Modifier.padding(28.dp)) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.24f)),
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center,
                    )
                    Button(onClick = onNewGame, shape = RoundedCornerShape(18.dp)) {
                        Text(text = stringResource(R.string.new_game))
                    }
                }
            }
        }
    }
}

@Composable
private fun FooterHint() {
    GlassPanel(modifier = Modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
            text = stringResource(R.string.play_hint),
            style = MaterialTheme.typography.bodyLarge,
            color = GameTokens.textMuted,
        )
    }
}

@Composable
private fun BackgroundBlobs() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp),
    ) {
        Box(
            modifier = Modifier
                .size(220.dp)
                .align(Alignment.TopEnd)
                .graphicsLayer(alpha = 0.42f)
                .background(
                    brush = Brush.radialGradient(
                        listOf(Color(0xFFFFD5B7), Color.Transparent),
                    ),
                    shape = CircleShape,
                ),
        )
        Box(
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.CenterStart)
                .graphicsLayer(alpha = 0.34f)
                .background(
                    brush = Brush.radialGradient(
                        listOf(Color(0xFFD8D4FF), Color.Transparent),
                    ),
                    shape = CircleShape,
                ),
        )
    }
}

private fun tileFontSize(value: Int) =
    when (value.toString().length) {
        1, 2 -> 34.sp
        3 -> 28.sp
        else -> 22.sp
    }

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA)
@Composable
private fun GameScreenPreview() {
    Game2048Theme {
        GameScreen(
            state = GameUiState(
                tiles = listOf(
                    GameTileUi(1, 2, 0, 0),
                    GameTileUi(2, 4, 0, 1),
                    GameTileUi(3, 16, 1, 1),
                    GameTileUi(4, 64, 2, 0),
                    GameTileUi(5, 256, 2, 2),
                    GameTileUi(6, 1024, 3, 3),
                ),
                score = 968,
                bestScore = 4096,
                moveCount = 12,
            ),
            onMove = {},
            onNewGame = {},
        )
    }
}
