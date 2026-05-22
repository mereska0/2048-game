package com.alexbyr.game2048.game.domain

const val BOARD_SIZE = 4
const val WINNING_VALUE = 2048

enum class MoveDirection {
    Up,
    Down,
    Left,
    Right,
}

enum class GameStatus {
    Active,
    Won,
    Lost,
}

data class Cell(
    val row: Int,
    val col: Int,
)

data class GameTile(
    val id: Int,
    val value: Int,
    val row: Int,
    val col: Int,
)

data class GameSnapshot(
    val tiles: List<GameTile>,
    val score: Int,
    val nextTileId: Int,
    val status: GameStatus,
)

data class MoveOutcome(
    val state: GameSnapshot,
    val moved: Boolean,
    val spawnedTileId: Int? = null,
    val mergedTileIds: Set<Int> = emptySet(),
    val largestCreatedValue: Int = 0,
)

data class SpawnSpec(
    val cell: Cell,
    val value: Int,
)

interface SpawnDecider {
    fun next(emptyCells: List<Cell>): SpawnSpec
}
