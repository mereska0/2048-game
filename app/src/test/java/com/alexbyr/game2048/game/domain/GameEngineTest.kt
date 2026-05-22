package com.alexbyr.game2048.game.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GameEngineTest {
    @Test
    fun moveLeftMergesOncePerPair() {
        val engine = GameEngine(QueueSpawnDecider(SpawnSpec(Cell(3, 3), 2)))
        val start = snapshotOf(
            GameTile(1, 2, 0, 0),
            GameTile(2, 2, 0, 1),
            GameTile(3, 2, 0, 2),
            GameTile(4, 2, 0, 3),
        )

        val result = engine.move(start, MoveDirection.Left)

        assertTrue(result.moved)
        assertEquals(8, result.state.score)
        assertEquals(
            listOf(
                GameTile(1, 4, 0, 0),
                GameTile(3, 4, 0, 1),
                GameTile(10, 2, 3, 3),
            ),
            result.state.tiles,
        )
    }

    @Test
    fun moveRightRepositionsTiles() {
        val engine = GameEngine(QueueSpawnDecider(SpawnSpec(Cell(3, 3), 2)))
        val start = snapshotOf(
            GameTile(1, 4, 0, 0),
            GameTile(2, 8, 1, 1),
        )

        val result = engine.move(start, MoveDirection.Right)

        assertTrue(result.moved)
        assertEquals(GameTile(1, 4, 0, 3), result.state.tiles.first { it.id == 1 })
        assertEquals(GameTile(2, 8, 1, 3), result.state.tiles.first { it.id == 2 })
    }

    @Test
    fun invalidMoveDoesNotSpawnNewTile() {
        val engine = GameEngine(QueueSpawnDecider(SpawnSpec(Cell(3, 3), 4)))
        val start = snapshotOf(
            GameTile(1, 2, 0, 0),
            GameTile(2, 4, 0, 1),
            GameTile(3, 8, 0, 2),
            GameTile(4, 16, 0, 3),
        )

        val result = engine.move(start, MoveDirection.Left)

        assertFalse(result.moved)
        assertNull(result.spawnedTileId)
        assertEquals(start, result.state)
    }

    @Test
    fun winningTileMarksGameWon() {
        val engine = GameEngine(QueueSpawnDecider(SpawnSpec(Cell(3, 3), 2)))
        val start = snapshotOf(
            GameTile(1, 1024, 0, 0),
            GameTile(2, 1024, 0, 1),
        )

        val result = engine.move(start, MoveDirection.Left)

        assertEquals(GameStatus.Won, result.state.status)
        assertTrue(result.mergedTileIds.contains(1))
    }

    @Test
    fun lockedBoardMarksGameLost() {
        val engine = GameEngine(QueueSpawnDecider(SpawnSpec(Cell(0, 3), 2)))
        val start = snapshotOf(
            GameTile(1, 2, 0, 0),
            GameTile(2, 4, 0, 1),
            GameTile(3, 2, 0, 2),
            GameTile(4, 4, 0, 3),
            GameTile(5, 4, 1, 0),
            GameTile(6, 2, 1, 1),
            GameTile(7, 4, 1, 2),
            GameTile(8, 2, 1, 3),
            GameTile(9, 2, 2, 0),
            GameTile(10, 4, 2, 1),
            GameTile(11, 2, 2, 2),
            GameTile(12, 4, 2, 3),
            GameTile(13, 4, 3, 0),
            GameTile(14, 2, 3, 1),
            GameTile(15, 4, 3, 2),
            GameTile(16, 2, 3, 3),
        )

        val lockedMove = engine.move(start, MoveDirection.Left)
        assertFalse(lockedMove.moved)
        assertEquals(GameStatus.Active, lockedMove.state.status)

        val preLoss = snapshotOf(
            GameTile(1, 2, 0, 0),
            GameTile(2, 2, 0, 1),
            GameTile(3, 8, 0, 2),
            GameTile(4, 16, 0, 3),
            GameTile(5, 32, 1, 0),
            GameTile(6, 64, 1, 1),
            GameTile(7, 128, 1, 2),
            GameTile(8, 256, 1, 3),
            GameTile(9, 512, 2, 0),
            GameTile(10, 1024, 2, 1),
            GameTile(11, 4, 2, 2),
            GameTile(12, 8, 2, 3),
            GameTile(13, 16, 3, 0),
            GameTile(14, 32, 3, 1),
            GameTile(15, 64, 3, 2),
            GameTile(16, 128, 3, 3),
        )
        val lossMove = engine.move(preLoss, MoveDirection.Left)
        assertTrue(lossMove.moved)
        assertEquals(GameStatus.Lost, lossMove.state.status)
    }

    private fun snapshotOf(vararg tiles: GameTile): GameSnapshot =
        GameSnapshot(
            tiles = tiles.toList().sortedWith(compareBy(GameTile::row, GameTile::col)),
            score = 0,
            nextTileId = 10,
            status = GameStatus.Active,
        )
}

private class QueueSpawnDecider(
    private vararg val spawns: SpawnSpec,
) : SpawnDecider {
    private var index = 0

    override fun next(emptyCells: List<Cell>): SpawnSpec {
        return spawns[index++]
    }
}
