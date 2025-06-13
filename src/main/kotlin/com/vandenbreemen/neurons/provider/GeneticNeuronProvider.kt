package com.vandenbreemen.neurons.provider

import com.vandenbreemen.neurons.model.*
import kotlin.random.Random

class GeneticNeuronProvider(
    private val geneList: Array<LongArray>
) : NeuronProvider {

    companion object {
        const val GENES_PER_NEURON = 3

        private fun generateGenomeForGridDimensions(rows: Int, cols: Int): Array<LongArray> {
            val geneList = Array(rows * cols) { LongArray(GENES_PER_NEURON) }
            for (i in 0 until rows * cols) {
                for (j in 0 until GENES_PER_NEURON) {
                    geneList[i][j] = Random.nextLong()
                }
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
    private val totalNeuronTypes = 5 // Regular, Motor, Sensory, Blinker, Pain Receptor

    override fun getNeuron(): Neuron {
        val gene = geneList[pointer]
        pointer++
        if (pointer >= geneList.size) {
            pointer = 0
        }

        return assembleNeuronBasedOnGene(gene)
    }

    private fun getActionIdFromGene(gene: LongArray): Byte {
        // Use bits 7-14 (8 bits) to determine actionId
        return ((gene[0] shr 7) and 0xFF).toByte()
    }

    private fun getSensorIdFromGene(gene: LongArray): Byte {
        // Use bits 15-22 (8 bits) to determine sensorId
        return ((gene[0] shr 15) and 0xFF).toByte()
    }

    private fun getLearningRateFromGene(gene: LongArray): Double {
        // Use bits 30-37 (8 bits) to determine learning rate (0-1.0 in increments of 1/1024)
        val incrementValue = ((gene[0] shr 30) and 0x3FF).toInt() // 10 bits = 1024 possible values
        return (incrementValue * (1.0 / 1024.0)).coerceIn(0.0, 1.0) // Ensure value is between 0 and 1
    }

    private fun getSigmoidExpDeltaFromGene(gene: LongArray): Double {
        // Use first 32 bits of the second long to determine sigmoidExpDelta (7.0 to 13.0)
        val incrementValue = ((gene[1] shr 0) and 0xFFFFFFFF).toInt() // 32 bits = 4,294,967,296 possible values
        return (incrementValue * (6.0 / 4294967295.0) + 7.0) // Map to range 7.0 to 13.0
    }

    private fun getSigmoidNumeratorMultiplierFromGene(gene: LongArray): Double {
        // Use second 32 bits of the second long to determine sigmoidNumeratorMultiplier (-5.0 to 5.0)
        val incrementValue = ((gene[1] shr 32) and 0xFFFFFFFF).toInt() // 32 bits = 4,294,967,296 possible values
        return (incrementValue * (10.0 / 4294967295.0) - 5.0).coerceIn(-5.0, 5.0) // Map to range -5.0 to 5.0
    }

    private fun assembleNeuronBasedOnGene(gene: LongArray): Neuron {
        val weightCalculator = StrengthBasedConnector

        val learningRate = getLearningRateFromGene(gene)

        // Use round-robin selection for neuron types
        val neuronType = (((gene[0] shr 0) and 0xF) % totalNeuronTypes).toInt()

        val neuron = when (neuronType) {
            0 -> Neuron(weightCalculator) // Regular Neuron
            1 -> MotorNeuron(getActionIdFromGene(gene), weightCalculator)
            2 -> SensoryNeuron(getSensorIdFromGene(gene), weightCalculator)
            3 -> {
                // For BlinkerNeuron, use bits 23-29 to determine turnsBeforeActivation (2-514 in increments of 1)
                val incrementValue = ((gene[0] shr 23) and 0x1FF).toInt() // 9 bits = 512 possible values
                BlinkerNeuron(2 + incrementValue, weightCalculator)
            }
            4 -> PainReceptorNeuron(weightCalculator)
            else -> Neuron(weightCalculator) // Fallback to regular neuron
        }.also {
            it.setLearningRate(learningRate)
            it.setSigmaExpDelta(getSigmoidExpDeltaFromGene(gene))
            it.setSigmoidNumeratorMultiplier(getSigmoidNumeratorMultiplierFromGene(gene))
        }

        return neuron
    }
}