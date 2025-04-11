package com.vandenbreemen.neurons.provider

import com.vandenbreemen.neurons.model.*
import kotlin.random.Random

class GeneticNeuronProvider(
    private val geneList: LongArray
) : NeuronProvider {

    companion object {
        private fun generateGenomeForGridDimensions(rows: Int, cols: Int): LongArray {

            val geneList = LongArray(rows * cols)
            for (i in 0 until rows * cols) {
                geneList[i] = Random.nextLong()
            }
            return geneList
        }

        fun generateGeneticProvider(
            rows: Int,
            cols: Int,
        ): GeneticNeuronProvider {
            val geneList = generateGenomeForGridDimensions(rows, cols)
            return GeneticNeuronProvider(geneList)
        }
    }

    private var pointer = 0

    override fun getNeuron(): Neuron {

        val gene = geneList[pointer]
        pointer++
        if (pointer >= geneList.size) {
            pointer = 0
        }

        return assembleNeuronBasedOnGene(gene)

    }

    private fun getDirectionFromGene(gene: Long): Direction {
        // Use bits 8-10 (3 bits) to determine direction
        val directionBits = (gene shr 8) and 0x7 // 0x7 is binary 111 (3 bits)
        return when (directionBits) {
            0L -> Direction.UP
            1L -> Direction.DOWN
            2L -> Direction.LEFT
            3L -> Direction.RIGHT
            4L -> Direction.UP_LEFT
            5L -> Direction.UP_RIGHT
            6L -> Direction.DOWN_LEFT
            7L -> Direction.DOWN_RIGHT
            else -> Direction.UP // Should never happen due to bit masking
        }
    }

    private fun getActionIdFromGene(gene: Long): Byte {
        // Use bits 11-18 (8 bits) to determine actionId
        return ((gene shr 11) and 0xFF).toByte()
    }

    private fun getSensorIdFromGene(gene: Long): Byte {
        // Use bits 19-26 (8 bits) to determine sensorId
        return ((gene shr 19) and 0xFF).toByte()
    }

    private fun getLearningRateFromGene(gene: Long): Double {
        // Use bits 34-40 (7 bits) to determine learning rate (0-1.27 in increments of 0.01)
        val incrementValue = ((gene shr 34) and 0x7F).toInt() // 7 bits = 128 possible values
        return (incrementValue * (1.0 / 128.0)).coerceIn(0.0, 1.0) // Ensure value is between 0 and 1
    }

    private fun assembleNeuronBasedOnGene(gene: Long): Neuron {
        //  Use the first 4 bits to determine the type of weight calculation
        val weightCalculatorType = gene and 0xF
        val weightCalculator = when (weightCalculatorType) {
            0L -> DefaultConnectionWeightCalculator
            1L -> StrengthBasedConnector
            else -> DefaultConnectionWeightCalculator
        }

        val learningRate = getLearningRateFromGene(gene)

        //  Use the next four bits to determine the type of neuron
        val neuronType = (gene shr 4) and 0xF
        val neuron = when (neuronType) {
            0L -> Neuron(weightCalculator)
            1L -> InhibitoryNeuron(weightCalculator)
            2L -> DeadNeuron(weightCalculator)
            3L -> FixedWeightNeuron(weightCalculator)
            4L -> RelayNeuron(weightCalculator)
            5L -> MotorNeuron(getActionIdFromGene(gene), weightCalculator)
            6L -> SensoryNeuron(getSensorIdFromGene(gene), weightCalculator)
            7L -> {
                // For BlinkerNeuron, use bits 27-33 to determine turnsBeforeActivation (2-200 in increments of 2)
                val incrementValue = ((gene shr 27) and 0x7F).toInt() // 7 bits = 128 possible values
                BlinkerNeuron(2 + (incrementValue * 2), weightCalculator)
            }
            8L -> {
                // For ThresholdNeuron, use bits 27-31 to determine threshold (0-1 in increments of 0.05)
                val thresholdIncrement = ((gene shr 27) and 0x1F).toInt() // 5 bits = 32 possible values
                val threshold =
                    (thresholdIncrement * (1.0 / 32.0)).coerceIn(0.0, 1.0) // Ensure value is between 0 and 1
                ThresholdNeuron(threshold, weightCalculator)
            }
            9L -> {
                PainReceptorNeuron(weightCalculator)
            }
            10L -> {
                Neuron(weightCalculator)
            }

            11L -> {
                InhibitoryNeuron(weightCalculator)
            }

            12L -> {
                DeadNeuron(weightCalculator)
            }

            13L -> {
                FixedWeightNeuron(weightCalculator)
            }

            14L -> {
                RelayNeuron(weightCalculator)
            }

            15L -> {
                // For RelayNeuron, use bits 27-33 to determine turnsBeforeActivation (2-200 in increments of 2)
                val incrementValue = ((gene shr 27) and 0x7F).toInt() // 7 bits = 128 possible values
                BlinkerNeuron(2 + (incrementValue * 2), weightCalculator)
            }

            else -> {
                val thresholdIncrement = ((gene shr 27) and 0x1F).toInt() // 5 bits = 32 possible values
                val threshold =
                    (thresholdIncrement * (1.0 / 32.0)).coerceIn(0.0, 1.0) // Ensure value is between 0 and 1
                ThresholdNeuron(threshold, weightCalculator)
            }
        }.also { it.setLearningRate(learningRate) }

        // If it's a RelayNeuron, store the direction in its metadata
        if (neuron is RelayNeuron) {
            neuron.direction = getDirectionFromGene(gene)
        }

        return neuron
    }
}