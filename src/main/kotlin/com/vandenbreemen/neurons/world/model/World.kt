package com.vandenbreemen.neurons.world.model

/**
 * Represents a 2D world with walls
 * @param width The width of the world in grid cells
 * @param height The height of the world in grid cells
 */
class World(
    val width: Int = 100,
    val height: Int = 100
) {
    private val grid: Array<Array<Boolean>> = Array(height) { Array(width) { false } }

    /**
     * Checks if a cell at the given coordinates is a wall
     * @param x The x coordinate (column)
     * @param y The y coordinate (row)
     * @return true if the cell is a wall, false otherwise
     */
    fun isWall(x: Int, y: Int): Boolean {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return true // Treat out-of-bounds as walls
        }
        return grid[y][x]
    }

    /**
     * Sets a cell at the given coordinates to be a wall
     * @param x The x coordinate (column)
     * @param y The y coordinate (row)
     * @param isWall true to make the cell a wall, false to make it empty
     */
    fun setWall(x: Int, y: Int, isWall: Boolean) {
        if (x in 0 until width && y in 0 until height) {
            grid[y][x] = isWall
        }
    }

    /**
     * Creates a wall in a straight line between two points
     * @param x1 Starting x coordinate
     * @param y1 Starting y coordinate
     * @param x2 Ending x coordinate
     * @param y2 Ending y coordinate
     */
    fun createWallLine(x1: Int, y1: Int, x2: Int, y2: Int) {
        val dx = x2 - x1
        val dy = y2 - y1
        val steps = maxOf(kotlin.math.abs(dx), kotlin.math.abs(dy))

        for (i in 0..steps) {
            val x = x1 + (dx * i / steps)
            val y = y1 + (dy * i / steps)
            setWall(x, y, true)
        }
    }

    /**
     * Creates a rectangular wall
     * @param x1 Top-left x coordinate
     * @param y1 Top-left y coordinate
     * @param x2 Bottom-right x coordinate
     * @param y2 Bottom-right y coordinate
     */
    fun createWallRectangle(x1: Int, y1: Int, x2: Int, y2: Int) {
        for (x in x1..x2) {
            for (y in y1..y2) {
                setWall(x, y, true)
            }
        }
    }
} 