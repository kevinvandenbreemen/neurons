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
    private val totalNeuronTypes = 5 // Regular, Motor, Sensory, Blinker, Pain Receptor

    override fun getNeuron(): Neuron {
        val gene = geneList[pointer]
        pointer++
        if (pointer >= geneList.size) {
            pointer = 0
        }

        return assembleNeuronBasedOnGene(gene)
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
        // Use bits 34-43 (10 bits) to determine learning rate (0-1.0 in increments of 1/1024)
        val incrementValue = ((gene shr 34) and 0x3FF).toInt() // 10 bits = 1024 possible values
        return (incrementValue * (1.0 / 1024.0)).coerceIn(0.0, 1.0) // Ensure value is between 0 and 1
    }

    private fun getSigmoidExpDeltaFromGene(gene: Long): Double {
        // Use bits 44-51 (8 bits) to determine sigmoidExpDelta (-3.0 to 3.0)
        val incrementValue = ((gene shr 44) and 0xFF).toInt() // 8 bits = 256 possible values
        return (incrementValue * (6.0 / 255.0) + 7.0) // Map to range 7.0 to 13.0
    }

    private fun getSigmoidNumeratorMultiplierFromGene(gene: Long): Double {
        // Use bits 52-59 (8 bits) to determine sigmoidNumeratorMultiplier (-5.0 to 5.0)
        val incrementValue = ((gene shr 52) and 0xFF).toInt() // 8 bits = 256 possible values
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

        // Use round-robin selection for neuron types
        val neuronType = (((gene shr 4) and 0xF) % totalNeuronTypes).toInt()

        val neuron = when (neuronType) {
            0 -> Neuron(weightCalculator) // Regular Neuron
            1 -> MotorNeuron(getActionIdFromGene(gene), weightCalculator)
            2 -> SensoryNeuron(getSensorIdFromGene(gene), weightCalculator)
            3 -> {
                // For BlinkerNeuron, use bits 27-35 to determine turnsBeforeActivation (2-514 in increments of 1)
                val incrementValue = ((gene shr 27) and 0x1FF).toInt() // 9 bits = 512 possible values
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