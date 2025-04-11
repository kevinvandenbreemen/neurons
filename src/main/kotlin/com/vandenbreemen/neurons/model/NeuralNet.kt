package com.vandenbreemen.neurons.model

import com.vandenbreemen.neurons.provider.DefaultNeuronProvider
import com.vandenbreemen.neurons.provider.NeuronProvider

class NeuralNet(val rows: Int, val cols: Int, val neuronProvider: NeuronProvider = DefaultNeuronProvider()) {
    private val grid: Array<Array<Neuron>> = Array(rows) { Array(cols) { neuronProvider.getNeuron() } }

    init {
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                val neuron = grid[i][j]

                // Connect to adjacent neurons
                if (i > 0) {
                    val targetNeuron = grid[i - 1][j]
                    val initialWeight = neuron.weightCalculator.calculateStartingConnectionWeight(neuron, targetNeuron)
                    neuron.connect(targetNeuron, initialWeight)

                    // If this is a RelayNeuron with UP direction, set the target
                    if (neuron is RelayNeuron && neuron.direction == Direction.UP) {
                        neuron.setTargetNeuron(targetNeuron)
                    }
                }
                if (i < rows - 1) {
                    val targetNeuron = grid[i + 1][j]
                    val initialWeight = neuron.weightCalculator.calculateStartingConnectionWeight(neuron, targetNeuron)
                    neuron.connect(targetNeuron, initialWeight)

                    // If this is a RelayNeuron with DOWN direction, set the target
                    if (neuron is RelayNeuron && neuron.direction == Direction.DOWN) {
                        neuron.setTargetNeuron(targetNeuron)
                    }
                }
                if (j > 0) {
                    val targetNeuron = grid[i][j - 1]
                    val initialWeight = neuron.weightCalculator.calculateStartingConnectionWeight(neuron, targetNeuron)
                    neuron.connect(targetNeuron, initialWeight)

                    // If this is a RelayNeuron with LEFT direction, set the target
                    if (neuron is RelayNeuron && neuron.direction == Direction.LEFT) {
                        neuron.setTargetNeuron(targetNeuron)
                    }
                }
                if (j < cols - 1) {
                    val targetNeuron = grid[i][j + 1]
                    val initialWeight = neuron.weightCalculator.calculateStartingConnectionWeight(neuron, targetNeuron)
                    neuron.connect(targetNeuron, initialWeight)

                    // If this is a RelayNeuron with RIGHT direction, set the target
                    if (neuron is RelayNeuron && neuron.direction == Direction.RIGHT) {
                        neuron.setTargetNeuron(targetNeuron)
                    }
                }
                if (i > 0 && j > 0) {
                    val targetNeuron = grid[i - 1][j - 1]
                    val initialWeight = neuron.weightCalculator.calculateStartingConnectionWeight(neuron, targetNeuron)
                    neuron.connect(targetNeuron, initialWeight)

                    // If this is a RelayNeuron with UP_LEFT direction, set the target
                    if (neuron is RelayNeuron && neuron.direction == Direction.UP_LEFT) {
                        neuron.setTargetNeuron(targetNeuron)
                    }
                }
                if (i > 0 && j < cols - 1) {
                    val targetNeuron = grid[i - 1][j + 1]
                    val initialWeight = neuron.weightCalculator.calculateStartingConnectionWeight(neuron, targetNeuron)
                    neuron.connect(targetNeuron, initialWeight)

                    // If this is a RelayNeuron with UP_RIGHT direction, set the target
                    if (neuron is RelayNeuron && neuron.direction == Direction.UP_RIGHT) {
                        neuron.setTargetNeuron(targetNeuron)
                    }
                }
                if (i < rows - 1 && j > 0) {
                    val targetNeuron = grid[i + 1][j - 1]
                    val initialWeight = neuron.weightCalculator.calculateStartingConnectionWeight(neuron, targetNeuron)
                    neuron.connect(targetNeuron, initialWeight)

                    // If this is a RelayNeuron with DOWN_LEFT direction, set the target
                    if (neuron is RelayNeuron && neuron.direction == Direction.DOWN_LEFT) {
                        neuron.setTargetNeuron(targetNeuron)
                    }
                }
                if (i < rows - 1 && j < cols - 1) {
                    val targetNeuron = grid[i + 1][j + 1]
                    val initialWeight = neuron.weightCalculator.calculateStartingConnectionWeight(neuron, targetNeuron)
                    neuron.connect(targetNeuron, initialWeight)

                    // If this is a RelayNeuron with DOWN_RIGHT direction, set the target
                    if (neuron is RelayNeuron && neuron.direction == Direction.DOWN_RIGHT) {
                        neuron.setTargetNeuron(targetNeuron)
                    }
                }
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