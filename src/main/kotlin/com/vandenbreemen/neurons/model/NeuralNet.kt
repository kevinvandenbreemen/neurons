package com.vandenbreemen.neurons.model

import com.vandenbreemen.neurons.provider.DefaultNeuronProvider
import com.vandenbreemen.neurons.provider.NeuronProvider

class NeuralNet(val rows: Int, val cols: Int, val neuronProvider: NeuronProvider = DefaultNeuronProvider()) {
    private val grid: Array<Array<Neuron>> = Array(rows) { Array(cols) { neuronProvider.getNeuron() } }

    init {
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                val neuron = grid[i][j]

                // Connect to adjacent neurons with toroidal wrapping
                // Up connection
                val upRow = if (i > 0) i - 1 else rows - 1
                val targetNeuron = grid[upRow][j]
                val initialWeight = neuron.weightCalculator.calculateStartingConnectionWeight(neuron, targetNeuron)
                neuron.connect(targetNeuron, initialWeight)
                if (neuron is RelayNeuron && neuron.direction == Direction.UP) {
                    neuron.setTargetNeuron(targetNeuron)
                }

                // Down connection
                val downRow = if (i < rows - 1) i + 1 else 0
                val downNeuron = grid[downRow][j]
                val downWeight = neuron.weightCalculator.calculateStartingConnectionWeight(neuron, downNeuron)
                neuron.connect(downNeuron, downWeight)
                if (neuron is RelayNeuron && neuron.direction == Direction.DOWN) {
                    neuron.setTargetNeuron(downNeuron)
                }

                // Left connection
                val leftCol = if (j > 0) j - 1 else cols - 1
                val leftNeuron = grid[i][leftCol]
                val leftWeight = neuron.weightCalculator.calculateStartingConnectionWeight(neuron, leftNeuron)
                neuron.connect(leftNeuron, leftWeight)
                if (neuron is RelayNeuron && neuron.direction == Direction.LEFT) {
                    neuron.setTargetNeuron(leftNeuron)
                }

                // Right connection
                val rightCol = if (j < cols - 1) j + 1 else 0
                val rightNeuron = grid[i][rightCol]
                val rightWeight = neuron.weightCalculator.calculateStartingConnectionWeight(neuron, rightNeuron)
                neuron.connect(rightNeuron, rightWeight)
                if (neuron is RelayNeuron && neuron.direction == Direction.RIGHT) {
                    neuron.setTargetNeuron(rightNeuron)
                }

                // Diagonal connections
                // Up-Left
                val upLeftRow = if (i > 0) i - 1 else rows - 1
                val upLeftCol = if (j > 0) j - 1 else cols - 1
                val upLeftNeuron = grid[upLeftRow][upLeftCol]
                val upLeftWeight = neuron.weightCalculator.calculateStartingConnectionWeight(neuron, upLeftNeuron)
                neuron.connect(upLeftNeuron, upLeftWeight)
                if (neuron is RelayNeuron && neuron.direction == Direction.UP_LEFT) {
                    neuron.setTargetNeuron(upLeftNeuron)
                }

                // Up-Right
                val upRightRow = if (i > 0) i - 1 else rows - 1
                val upRightCol = if (j < cols - 1) j + 1 else 0
                val upRightNeuron = grid[upRightRow][upRightCol]
                val upRightWeight = neuron.weightCalculator.calculateStartingConnectionWeight(neuron, upRightNeuron)
                neuron.connect(upRightNeuron, upRightWeight)
                if (neuron is RelayNeuron && neuron.direction == Direction.UP_RIGHT) {
                    neuron.setTargetNeuron(upRightNeuron)
                }

                // Down-Left
                val downLeftRow = if (i < rows - 1) i + 1 else 0
                val downLeftCol = if (j > 0) j - 1 else cols - 1
                val downLeftNeuron = grid[downLeftRow][downLeftCol]
                val downLeftWeight = neuron.weightCalculator.calculateStartingConnectionWeight(neuron, downLeftNeuron)
                neuron.connect(downLeftNeuron, downLeftWeight)
                if (neuron is RelayNeuron && neuron.direction == Direction.DOWN_LEFT) {
                    neuron.setTargetNeuron(downLeftNeuron)
                }

                // Down-Right
                val downRightRow = if (i < rows - 1) i + 1 else 0
                val downRightCol = if (j < cols - 1) j + 1 else 0
                val downRightNeuron = grid[downRightRow][downRightCol]
                val downRightWeight = neuron.weightCalculator.calculateStartingConnectionWeight(neuron, downRightNeuron)
                neuron.connect(downRightNeuron, downRightWeight)
                if (neuron is RelayNeuron && neuron.direction == Direction.DOWN_RIGHT) {
                    neuron.setTargetNeuron(downRightNeuron)
                }
            }
        }
    }

    fun getCellAt(row: Int, col: Int): Neuron {
        return grid[row][col]
    }

    private fun getDestination(row: Int, col: Int, direction: Direction): Pair<Int, Int> {
        return when (direction) {
            Direction.UP -> (if (row > 0) row - 1 else rows - 1) to col
            Direction.DOWN -> (if (row < rows - 1) row + 1 else 0) to col
            Direction.LEFT -> row to (if (col > 0) col - 1 else cols - 1)
            Direction.RIGHT -> row to (if (col < cols - 1) col + 1 else 0)
            Direction.UP_LEFT -> (if (row > 0) row - 1 else rows - 1) to (if (col > 0) col - 1 else cols - 1)
            Direction.UP_RIGHT -> (if (row > 0) row - 1 else rows - 1) to (if (col < cols - 1) col + 1 else 0)
            Direction.DOWN_LEFT -> (if (row < rows - 1) row + 1 else 0) to (if (col > 0) col - 1 else cols - 1)
            Direction.DOWN_RIGHT -> (if (row < rows - 1) row + 1 else 0) to (if (col < cols - 1) col + 1 else 0)
        }
    }

    fun getConnectionStrengthFrom(row: Int, col: Int, direction: Direction): Double {
        val (destRow, destCol) = getDestination(row, col, direction)
        val neuron = grid[row][col]
        val connection = neuron.connections.find { it.neuron == grid[destRow][destCol] }
        return connection?.weight ?: 0.0
    }

    /**
     * Fires all neurons in the network and then applies their updates.
     * This is done in two phases to ensure all neurons fire based on their current state
     * before any updates are applied.
     */
    fun fireAndUpdate() {
        // First phase: Fire all neurons
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                grid[i][j].fire()
            }
        }

        // Second phase: Apply all stimulation updates
        applyAll()
    }

    fun applyAll() {
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                grid[i][j].applyStimulation()
            }
        }
    }

    /**
     * Updates connection weights across the entire network based on current neuron activations
     */
    fun updateAllWeights(learningRate: Double = 0.1) {
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                grid[i][j].apply {
                    val lrnRate = if (this.learningRateOverride != 0.0) this.learningRateOverride else learningRate
                    updateAllConnectionWeights(lrnRate)
                }
            }
        }
    }
}