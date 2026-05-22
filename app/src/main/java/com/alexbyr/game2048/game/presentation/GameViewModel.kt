package com.alexbyr.game2048.game.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alexbyr.game2048.game.domain.GameEngine
import com.alexbyr.game2048.game.domain.GameSnapshot
import com.alexbyr.game2048.game.domain.GameStatus
import com.alexbyr.game2048.game.domain.GameTile
import com.alexbyr.game2048.game.domain.MoveDirection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GameTileUi(
    val id: Int,
    val value: Int,
    val row: Int,
    val col: Int,
    val previousRow: Int = row,
    val previousCol: Int = col,
)

data class GameUiState(
    val tiles: List<GameTileUi> = emptyList(),
    val score: Int = 0,
    val bestScore: Int = 0,
    val status: GameStatus = GameStatus.Active,
    val moveCount: Int = 0,
    val spawnedTileId: Int? = null,
    val mergedTileIds: Set<Int> = emptySet(),
    val largeMergePulse: Boolean = false,
)

class GameViewModel(
    private val bestScoreStore: BestScoreStore,
    private val engine: GameEngine = GameEngine(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var snapshot: GameSnapshot = engine.newGame()

    init {
        syncUi(snapshot = snapshot)
        viewModelScope.launch {
            bestScoreStore.bestScore.collect { storedBest ->
                _uiState.update { state ->
                    state.copy(bestScore = maxOf(storedBest, snapshot.score))
                }
            }
        }
    }

    fun onMove(direction: MoveDirection) {
        val outcome = engine.move(snapshot, direction)
        if (!outcome.moved) return

        snapshot = outcome.state
        syncUi(
            snapshot = snapshot,
            spawnedTileId = outcome.spawnedTileId,
            mergedTileIds = outcome.mergedTileIds,
            largeMergePulse = outcome.largestCreatedValue >= 128,
            incrementMove = true,
        )
        persistBestScoreIfNeeded()
    }

    fun newGame() {
        snapshot = engine.newGame()
        syncUi(snapshot = snapshot, resetMoveCount = true)
    }

    private fun persistBestScoreIfNeeded() {
        val score = snapshot.score
        if (score <= _uiState.value.bestScore) return
        viewModelScope.launch {
            bestScoreStore.saveBestScore(score)
        }
    }

    private fun syncUi(
        snapshot: GameSnapshot,
        spawnedTileId: Int? = null,
        mergedTileIds: Set<Int> = emptySet(),
        largeMergePulse: Boolean = false,
        incrementMove: Boolean = false,
        resetMoveCount: Boolean = false,
    ) {
        _uiState.update { state ->
            val previousTiles = state.tiles.associateBy(GameTileUi::id)
            state.copy(
                tiles = snapshot.tiles.map { tile ->
                    val previous = previousTiles[tile.id]
                    tile.toUiTile(previous = previous)
                },
                score = snapshot.score,
                status = snapshot.status,
                spawnedTileId = spawnedTileId,
                mergedTileIds = mergedTileIds,
                largeMergePulse = largeMergePulse,
                moveCount = when {
                    resetMoveCount -> 0
                    incrementMove -> state.moveCount + 1
                    else -> state.moveCount
                },
            )
        }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return GameViewModel(
                        bestScoreStore = PreferencesBestScoreStore(context.applicationContext),
                    ) as T
                }
            }
    }
}

private fun GameTile.toUiTile(): GameTileUi =
    toUiTile(previous = null)

private fun GameTile.toUiTile(previous: GameTileUi?): GameTileUi =
    GameTileUi(
        id = id,
        value = value,
        row = row,
        col = col,
        previousRow = previous?.row ?: row,
        previousCol = previous?.col ?: col,
    )
