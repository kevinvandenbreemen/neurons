package com.vandenbreemen.neurons.model

class NeuralNet(val rows: Int, val cols: Int) {
    private val grid: Array<Array<Neuron>> = Array(rows) { Array(cols) { Neuron() } }

    init {
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                val neuron = grid[i][j]
                if (i > 0) neuron.connect(grid[i - 1][j]) // Connect to the neuron above
                if (i < rows - 1) neuron.connect(grid[i + 1][j]) // Connect to the neuron below
                if (j > 0) neuron.connect(grid[i][j - 1]) // Connect to the neuron to the left
                if (j < cols - 1) neuron.connect(grid[i][j + 1]) // Connect to the neuron to the right
                if (i > 0 && j > 0) neuron.connect(grid[i - 1][j - 1]) // Connect to the neuron above-left
                if (i > 0 && j < cols - 1) neuron.connect(grid[i - 1][j + 1]) // Connect to the neuron above-right
                if (i < rows - 1 && j > 0) neuron.connect(grid[i + 1][j - 1]) // Connect to the neuron below-left
                if (i < rows - 1 && j < cols - 1) neuron.connect(grid[i + 1][j + 1]) // Connect to the neuron below-right

            }
        }
    }

    fun getCellAt(row: Int, col: Int): Neuron {
        return grid[row][col]
    }

    private fun getDestination(row: Int, col: Int, direction: Direction): Pair<Int, Int>? {
        return when (direction) {
            Direction.UP -> if (row > 0) row - 1 to col else null
            Direction.DOWN -> if (row < rows - 1) row + 1 to col else null
            Direction.LEFT -> if (col > 0) row to col - 1 else null
            Direction.RIGHT -> if (col < cols - 1) row to col + 1 else null
            Direction.UP_LEFT -> if (row > 0 && col > 0) row - 1 to col - 1 else null
            Direction.UP_RIGHT -> if (row > 0 && col < cols - 1) row - 1 to col + 1 else null
            Direction.DOWN_LEFT -> if (row < rows - 1 && col > 0) row + 1 to col - 1 else null
            Direction.DOWN_RIGHT -> if (row < rows - 1 && col < cols - 1) row + 1 to col + 1 else null
        }
    }

    fun getConnectionStrengthFrom(row: Int, col: Int, direction: Direction): Double {

        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return Double.MIN_VALUE
        }

        val destination = getDestination(row, col, direction) ?: return Double.MIN_VALUE


        val (destRow, destCol) = destination
        val neuron = grid[row][col]
        val connection = neuron.connections.find { it.neuron == grid[destRow][destCol] }
        return connection?.strength ?: 0.0
    }
}