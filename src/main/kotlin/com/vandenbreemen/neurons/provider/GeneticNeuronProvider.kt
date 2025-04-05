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

    private fun assembleNeuronBasedOnGene(gene: Long): Neuron {
        //  Use the first 4 bits to determine the type of weight calculation
        val weightCalculatorType = gene and 0xF
        val weightCalculator = when (weightCalculatorType) {
            0L -> DefaultConnectionWeightCalculator
            1L -> StrengthBasedConnector
            else -> DefaultConnectionWeightCalculator
        }

        //  Use the next four bits to determine the type of neuron
        val neuronType = (gene shr 4) and 0xF
        val neuron = when (neuronType) {
            0L -> Neuron(weightCalculator)
            1L -> InhibitoryNeuron(weightCalculator)
            2L -> SineNeuron(0.01f, weightCalculator)
            3L -> FixedWeightNeuron(weightCalculator)
            4L -> RelayNeuron(weightCalculator)
            else -> Neuron(weightCalculator)
        }

        // If it's a RelayNeuron, store the direction in its metadata
        if (neuron is RelayNeuron) {
            neuron.direction = getDirectionFromGene(gene)
        }

        return neuron
    }
}