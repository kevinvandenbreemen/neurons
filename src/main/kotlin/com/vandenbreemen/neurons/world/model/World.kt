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

    fun isOutOfBounds(x: Int, y: Int): Boolean {
        return x < 0 || x >= width || y < 0 || y >= height
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

    fun getRandomEmptyCell(): AgentPosition {
        val emptyCells = mutableListOf<AgentPosition>()
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (!isWall(x, y)) {
                    emptyCells.add(AgentPosition(x, y))
                }
            }
        }
        if (emptyCells.isEmpty()) {
            throw IllegalStateException("No empty cells available in the world.")
        }
        return emptyCells.random()
    }

    companion object {
        /**
         * Creates a random world with walls
         * @param width The width of the world in grid cells
         * @param height The height of the world in grid cells
         * @param wallDensity The probability (0.0 to 1.0) of a cell being a wall
         * @param minRoomSize The minimum size of rectangular rooms
         * @param maxRoomSize The maximum size of rectangular rooms
         * @param numRooms The number of rectangular rooms to generate
         * @param numRandomWalls The number of random diagonal walls to generate
         * @return A new World instance with random walls
         */
        fun randomWorld(
            width: Int = 100,
            height: Int = 100,
            wallDensity: Double = 0.004,
            minRoomSize: Int = 5,
            maxRoomSize: Int = 15,
            numRooms: Int = 5,
            numRandomWalls: Int = 3
        ): World {
            require(wallDensity in 0.0..1.0) { "Wall density must be between 0 and 1" }
            require(minRoomSize > 0) { "Minimum room size must be positive" }
            require(maxRoomSize >= minRoomSize) { "Maximum room size must be greater than or equal to minimum room size" }

            val world = World(width, height)
            val random = kotlin.random.Random

            // Generate random rectangular rooms
            repeat(numRooms) {
                val roomWidth = random.nextInt(minRoomSize, maxRoomSize)
                val roomHeight = random.nextInt(minRoomSize, maxRoomSize)
                val x1 = random.nextInt(0, width - roomWidth)
                val y1 = random.nextInt(0, height - roomHeight)
                world.createWallRectangle(x1, y1, x1 + roomWidth, y1 + roomHeight)
            }

            // Generate random diagonal walls
            repeat(numRandomWalls) {
                val x1 = random.nextInt(0, width)
                val y1 = random.nextInt(0, height)
                val x2 = random.nextInt(0, width)
                val y2 = random.nextInt(0, height)
                world.createWallLine(x1, y1, x2, y2)
            }

            // Add random scattered walls based on wall density
            for (x in 0 until width) {
                for (y in 0 until height) {
                    if (random.nextDouble() < wallDensity) {
                        world.setWall(x, y, true)
                    }
                }
            }

            return world
        }
    }
} 