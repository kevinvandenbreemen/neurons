package com.vandenbreemen.neurons.evolution.fitness

import com.vandenbreemen.neurons.evolution.model.GeneticPool
import com.vandenbreemen.neurons.model.NeuralNet
import com.vandenbreemen.neurons.provider.GeneticNeuronProvider

open class GeneticFitnessDriver(
    private val brainSizeX: Int,
    private val brainSizeY: Int,
    private val numGenes: Int,
    mutationRate: Double = 0.1,
    private val eliteSize: Int = 5,
    private val numEpochs: Int = 10,
    private val newGeneProbability: Double = 0.1,
    existingGenePool: GeneticPool? = null
) {
    private val genePool = existingGenePool ?: GeneticPool(
        brainSizeX, brainSizeY, numGenes, mutationRate
    )

    fun getGenePool(): GeneticPool {
        return genePool
    }

    fun drive(
        fitnessFunction: (geneticNeuronProvider: GeneticNeuronProvider) -> Double,
        onEpochComplete: (Int, Double) -> Unit = { _, _ -> },
    ) {
        var bestScore = 0.0
        for (i in 0 until numEpochs) {
            val score = iterate() { geneticNeuronProvider ->
                fitnessFunction(geneticNeuronProvider)
            }
            if (score > bestScore) {
                bestScore = score
            }

            //  If there has been no non-zero score then none of the genes is anywhere close so dump the pool
            if (score <= 0.0) {
                genePool.reinitialize()
            } else {
                //  If the score is non-zero then we can keep the pool
                genePool.evolve(numGenes, eliteSize, newGeneProbability = newGeneProbability)
            }


            onEpochComplete(i + 1, bestScore)
        }
    }

    fun getRandomNeuralNetwork(): NeuralNet {
        return NeuralNet(
            brainSizeX,
            brainSizeY,
            genePool.getRandomProvider()
        )
    }

    private fun iterate(
        fitnessFunction: (geneticNeuronProvider: GeneticNeuronProvider) -> Double
    ): Double {

        var bestScore = 0.0

        genePool.forEachProvider { indexInPool, geneticNeuronProvider ->
            val score = fitnessFunction(geneticNeuronProvider)
            if (score > bestScore) {
                bestScore = score
            }
            genePool.setFitness(indexInPool, score)
        }

        return bestScore
    }
}