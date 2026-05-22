package com.alexbyr.game2048.game.presentation

import com.alexbyr.game2048.game.domain.Cell
import com.alexbyr.game2048.game.domain.GameEngine
import com.alexbyr.game2048.game.domain.MoveDirection
import com.alexbyr.game2048.game.domain.SpawnDecider
import com.alexbyr.game2048.game.domain.SpawnSpec
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun restartResetsBoardAndPreservesBestScore() = runTest {
        val store = FakeBestScoreStore(initial = 32)
        val viewModel = GameViewModel(
            bestScoreStore = store,
            engine = GameEngine(
                spawnDecider = SequenceSpawnDecider(
                    SpawnSpec(Cell(0, 0), 2),
                    SpawnSpec(Cell(0, 1), 2),
                    SpawnSpec(Cell(3, 3), 4),
                    SpawnSpec(Cell(2, 2), 2),
                    SpawnSpec(Cell(0, 0), 2),
                    SpawnSpec(Cell(0, 1), 2),
                ),
            ),
        )

        advanceUntilIdle()
        viewModel.onMove(MoveDirection.Left)
        advanceUntilIdle()
        assertEquals(4, viewModel.uiState.value.score)
        assertEquals(32, viewModel.uiState.value.bestScore)

        viewModel.newGame()
        advanceUntilIdle()

        assertEquals(0, viewModel.uiState.value.score)
        assertEquals(32, viewModel.uiState.value.bestScore)
        assertEquals(2, viewModel.uiState.value.tiles.size)
    }
}

private class FakeBestScoreStore(initial: Int) : BestScoreStore {
    private val flow = MutableStateFlow(initial)

    override val bestScore: Flow<Int> = flow

    override suspend fun saveBestScore(score: Int) {
        flow.value = maxOf(flow.value, score)
    }
}

private class SequenceSpawnDecider(
    private vararg val values: SpawnSpec,
) : SpawnDecider {
    private var index = 0

    override fun next(emptyCells: List<Cell>): SpawnSpec = values[index++]
}
