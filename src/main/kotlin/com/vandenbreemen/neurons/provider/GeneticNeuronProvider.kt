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

    private fun getSigmoidExpDeltaFromGene(gene: Long): Double {
        // Use bits 41-48 (8 bits) to determine sigmoidExpDelta (-3.0 to 3.0)
        val incrementValue = ((gene shr 41) and 0xFF).toInt() // 8 bits = 256 possible values
        return (incrementValue * (6.0 / 255.0) - 3.0).coerceIn(-3.0, 3.0) // Map to range -3.0 to 3.0
    }

    private fun getSigmoidNumeratorMultiplierFromGene(gene: Long): Double {
        // Use bits 49-56 (8 bits) to determine sigmoidNumeratorMultiplier (-5.0 to 5.0)
        val incrementValue = ((gene shr 49) and 0xFF).toInt() // 8 bits = 256 possible values
        return (incrementValue * (10.0 / 255.0) - 5.0).coerceIn(-5.0, 5.0) // Map to range -5.0 to 5.0
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
            1L -> DeadNeuron(weightCalculator)
            2L -> RelayNeuron(weightCalculator)
            3L -> MotorNeuron(getActionIdFromGene(gene), weightCalculator)
            4L -> SensoryNeuron(getSensorIdFromGene(gene), weightCalculator)
            5L -> {
                // For BlinkerNeuron, use bits 27-33 to determine turnsBeforeActivation (2-200 in increments of 2)
                val incrementValue = ((gene shr 27) and 0x7F).toInt() // 7 bits = 128 possible values
                BlinkerNeuron(2 + (incrementValue * 2), weightCalculator)
            }

            6L -> {
                PainReceptorNeuron(weightCalculator)
            }

            7L -> {
                Neuron(weightCalculator)
            }

            8L -> {
                DeadNeuron(weightCalculator)
            }

            9L -> {
                RelayNeuron(weightCalculator)
            }
            10L -> {
                MotorNeuron(getActionIdFromGene(gene), weightCalculator)
            }
            11L -> {
                SensoryNeuron(getSensorIdFromGene(gene), weightCalculator)
            }
            12L -> {
                // For BlinkerNeuron, use bits 27-33 to determine turnsBeforeActivation (2-200 in increments of 2)
                val incrementValue = ((gene shr 27) and 0x7F).toInt() // 7 bits = 128 possible values
                BlinkerNeuron(2 + (incrementValue * 2), weightCalculator)
            }
            13L -> {
                PainReceptorNeuron(weightCalculator)
            }
            14L -> {
                Neuron(weightCalculator)
            }
            15L -> {
                DeadNeuron(weightCalculator)
            }
            else -> {
                Neuron(weightCalculator)
            }
        }.also {
            it.setLearningRate(learningRate)
            it.setSigmaExpDelta(getSigmoidExpDeltaFromGene(gene))
            it.setSigmoidNumeratorMultiplier(getSigmoidNumeratorMultiplierFromGene(gene))
        }

        // If it's a RelayNeuron, store the direction in its metadata
        if (neuron is RelayNeuron) {
            neuron.direction = getDirectionFromGene(gene)
        }

        return neuron
    }
}