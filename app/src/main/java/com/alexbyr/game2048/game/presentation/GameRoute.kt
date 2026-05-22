package com.alexbyr.game2048.game.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alexbyr.game2048.ui.game.GameScreen

@Composable
fun GameRoute() {
    val context = LocalContext.current
    val viewModel: GameViewModel = viewModel(factory = GameViewModel.factory(context))
    val state = viewModel.uiState.collectAsStateWithLifecycle()

    GameScreen(
        state = state.value,
        onMove = viewModel::onMove,
        onNewGame = viewModel::newGame,
    )
}
