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
            }
        }
    }
}