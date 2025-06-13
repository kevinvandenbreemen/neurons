package com.vandenbreemen.neurons.provider

import com.vandenbreemen.neurons.model.*
import kotlin.random.Random

class GeneticNeuronProvider(
    private val geneList: Array<LongArray>
) : NeuronProvider {

    companion object {
        const val GENES_PER_NEURON = 4

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
        val min = 0.0
        val max = 2.0
        val incrementValue = ((gene[2] shr 0) and 0xFFFFFFFF).toDouble() // 32 bits = 4,294,967,296 possible values
        return (incrementValue * ((max - min) / 4294967295.0) + min).coerceIn(min, max) // Map to range -1.0 to 1.0
    }

    private fun getSigmoidExpDeltaFromGene(gene: LongArray): Double {
        val min = -25.0
        val max = 25.0
        val incrementValue = ((gene[1] shr 0) and 0xFFFFFFFF).toDouble() // 32 bits = 4,294,967,296 possible values
        return (incrementValue * ((max - min) / 4294967295.0) + min) // Map to range 7.0 to 13.0
    }

    private fun getSigmoidNumeratorMultiplierFromGene(gene: LongArray): Double {
        val min = -2.0
        val max = 2.0
        val incrementValue = ((gene[1] shr 32) and 0xFFFFFFFF).toDouble() // 32 bits = 4,294,967,296 possible values
        return (incrementValue * ((max - min) / 4294967295.0) + min).coerceIn(min, max) // Map to range -5.0 to 5.0
    }

    private fun getSigmoidNumeratorFromGene(gene: LongArray, multiplier: Double): Double {
        val incrementValue = ((gene[2] shr 32) and 0xFFFFFFFF).toDouble() // 32 bits = 4,294,967,296 possible values
        return (incrementValue * (multiplier / 4294967295.0))
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
            it.sigmoidNumerator = (getSigmoidNumeratorFromGene(gene, it.maxActivationValue))
        }

        return neuron
    }
}