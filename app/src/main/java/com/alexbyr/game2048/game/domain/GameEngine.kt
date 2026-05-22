package com.alexbyr.game2048.game.domain

import kotlin.math.max
import kotlin.random.Random

class GameEngine(
    private val spawnDecider: SpawnDecider = RandomSpawnDecider(),
) {
    fun newGame(): GameSnapshot {
        var state = GameSnapshot(
            tiles = emptyList(),
            score = 0,
            nextTileId = 1,
            status = GameStatus.Active,
        )
        state = spawnIntoState(state).first
        state = spawnIntoState(state).first
        return state
    }

    fun move(state: GameSnapshot, direction: MoveDirection): MoveOutcome {
        if (state.status != GameStatus.Active) {
            return MoveOutcome(state = state, moved = false)
        }

        val board = state.tiles.associateBy { Cell(it.row, it.col) }
        val resultTiles = mutableListOf<GameTile>()
        val mergedTileIds = mutableSetOf<Int>()
        var scoreGain = 0
        var largestCreatedValue = 0
        var moved = false

        lineSpecs(direction).forEach { cells ->
            val tilesInLine = cells.mapNotNull(board::get)
            val lineMove = moveLine(tilesInLine)
            scoreGain += lineMove.scoreGain
            largestCreatedValue = max(largestCreatedValue, lineMove.largestCreatedValue)
            mergedTileIds += lineMove.mergedTileIds

            lineMove.tiles.forEachIndexed { index, tile ->
                val target = cells[index]
                if (tile.row != target.row || tile.col != target.col) {
                    moved = true
                }
                resultTiles += tile.copy(row = target.row, col = target.col)
            }
            if (lineMove.tiles.size != tilesInLine.size) {
                moved = true
            }
        }

        if (!moved) {
            return MoveOutcome(state = state, moved = false)
        }

        val movedState = GameSnapshot(
            tiles = resultTiles.sortedWith(compareBy(GameTile::row, GameTile::col)),
            score = state.score + scoreGain,
            nextTileId = state.nextTileId,
            status = GameStatus.Active,
        )

        val (spawnedState, spawnedTileId) = spawnIntoState(movedState)
        val finalState = spawnedState.copy(status = deriveStatus(spawnedState.tiles))

        return MoveOutcome(
            state = finalState,
            moved = true,
            spawnedTileId = spawnedTileId,
            mergedTileIds = mergedTileIds,
            largestCreatedValue = largestCreatedValue,
        )
    }

    private fun moveLine(tilesInLine: List<GameTile>): LineMoveResult {
        if (tilesInLine.isEmpty()) {
            return LineMoveResult(emptyList(), emptySet(), 0, 0)
        }

        val compacted = mutableListOf<GameTile>()
        val mergedIds = mutableSetOf<Int>()
        var index = 0
        var scoreGain = 0
        var largestCreatedValue = 0

        while (index < tilesInLine.size) {
            val current = tilesInLine[index]
            val next = tilesInLine.getOrNull(index + 1)
            if (next != null && next.value == current.value) {
                val mergedValue = current.value * 2
                compacted += current.copy(value = mergedValue)
                mergedIds += current.id
                scoreGain += mergedValue
                largestCreatedValue = max(largestCreatedValue, mergedValue)
                index += 2
            } else {
                compacted += current
                index += 1
            }
        }

        return LineMoveResult(
            tiles = compacted,
            mergedTileIds = mergedIds,
            scoreGain = scoreGain,
            largestCreatedValue = largestCreatedValue,
        )
    }

    private fun lineSpecs(direction: MoveDirection): List<List<Cell>> =
        when (direction) {
            MoveDirection.Left -> (0 until BOARD_SIZE).map { row ->
                (0 until BOARD_SIZE).map { col -> Cell(row, col) }
            }

            MoveDirection.Right -> (0 until BOARD_SIZE).map { row ->
                (BOARD_SIZE - 1 downTo 0).map { col -> Cell(row, col) }
            }

            MoveDirection.Up -> (0 until BOARD_SIZE).map { col ->
                (0 until BOARD_SIZE).map { row -> Cell(row, col) }
            }

            MoveDirection.Down -> (0 until BOARD_SIZE).map { col ->
                (BOARD_SIZE - 1 downTo 0).map { row -> Cell(row, col) }
            }
        }

    private fun spawnIntoState(state: GameSnapshot): Pair<GameSnapshot, Int?> {
        val occupied = state.tiles.map { Cell(it.row, it.col) }.toSet()
        val emptyCells = buildList {
            repeat(BOARD_SIZE) { row ->
                repeat(BOARD_SIZE) { col ->
                    val cell = Cell(row, col)
                    if (cell !in occupied) add(cell)
                }
            }
        }
        if (emptyCells.isEmpty()) {
            return state to null
        }

        val spawn = spawnDecider.next(emptyCells)
        require(spawn.cell in emptyCells) { "Spawn decider returned occupied cell ${spawn.cell}" }

        val tile = GameTile(
            id = state.nextTileId,
            value = spawn.value,
            row = spawn.cell.row,
            col = spawn.cell.col,
        )
        return state.copy(
            tiles = (state.tiles + tile).sortedWith(compareBy(GameTile::row, GameTile::col)),
            nextTileId = state.nextTileId + 1,
        ) to tile.id
    }

    private fun deriveStatus(tiles: List<GameTile>): GameStatus {
        if (tiles.any { it.value >= WINNING_VALUE }) {
            return GameStatus.Won
        }
        return if (hasMovesAvailable(tiles)) GameStatus.Active else GameStatus.Lost
    }

    private fun hasMovesAvailable(tiles: List<GameTile>): Boolean {
        if (tiles.size < BOARD_SIZE * BOARD_SIZE) {
            return true
        }
        val board = tiles.associateBy { Cell(it.row, it.col) }
        tiles.forEach { tile ->
            val right = board[Cell(tile.row, tile.col + 1)]
            val down = board[Cell(tile.row + 1, tile.col)]
            if (right?.value == tile.value || down?.value == tile.value) {
                return true
            }
        }
        return false
    }

    private data class LineMoveResult(
        val tiles: List<GameTile>,
        val mergedTileIds: Set<Int>,
        val scoreGain: Int,
        val largestCreatedValue: Int,
    )
}

class RandomSpawnDecider(
    private val random: Random = Random.Default,
) : SpawnDecider {
    override fun next(emptyCells: List<Cell>): SpawnSpec {
        require(emptyCells.isNotEmpty()) { "emptyCells must not be empty" }
        val cell = emptyCells[random.nextInt(emptyCells.size)]
        val value = if (random.nextFloat() < 0.9f) 2 else 4
        return SpawnSpec(cell = cell, value = value)
    }
}
