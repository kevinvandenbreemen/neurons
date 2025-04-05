package com.vandenbreemen.neurons.model

/**
 * Represents the 8 possible directions around a neuron in the grid.
 * Each direction corresponds to a possible connection to an adjacent neuron.
 */
enum class Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT,
    UP_LEFT,
    UP_RIGHT,
    DOWN_LEFT,
    DOWN_RIGHT;

    /**
     * Returns the row and column offsets for this direction.
     * @return A Pair where the first value is the row offset and the second is the column offset.
     */
    fun getOffsets(): Pair<Int, Int> = when (this) {
        UP -> -1 to 0
        DOWN -> 1 to 0
        LEFT -> 0 to -1
        RIGHT -> 0 to 1
        UP_LEFT -> -1 to -1
        UP_RIGHT -> -1 to 1
        DOWN_LEFT -> 1 to -1
        DOWN_RIGHT -> 1 to 1
    }
}