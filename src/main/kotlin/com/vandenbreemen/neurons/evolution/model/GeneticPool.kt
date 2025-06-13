package com.vandenbreemen.neurons.evolution.model

import com.vandenbreemen.neurons.provider.GeneticNeuronProvider
import kotlin.random.Random

class GeneticPool(
    private val rows: Int,
    private val cols: Int,
    private val poolSize: Int,
    private val mutationRate: Double = 0.1
) {

    private var pool: List<Array<LongArray>> = List(poolSize) { generateGenome() }
    private var fitnessScores: List<Double> = List(poolSize) { 0.0 }

    private fun generateGenome(): Array<LongArray> {
        val geneList = Array(rows * cols) { LongArray(GeneticNeuronProvider.GENES_PER_NEURON) }
        for (i in 0 until rows * cols) {
            for (j in 0 until GeneticNeuronProvider.GENES_PER_NEURON) {
                geneList[i][j] = Random.nextLong()
            }
        }
        return geneList
    }

    private fun crossover(parent1Index: Int, parent2Index: Int): Array<LongArray> {
        require(parent1Index in 0 until poolSize && parent2Index in 0 until poolSize) { "Parent indices out of bounds" }

        val parent1 = pool[parent1Index]
        val parent2 = pool[parent2Index]
        val child = Array(parent1.size) { LongArray(GeneticNeuronProvider.GENES_PER_NEURON) }

        // Perform single-point crossover
        val crossoverPoint = Random.nextInt(parent1.size)
        for (i in child.indices) {
            if (i < crossoverPoint) {
                child[i] = parent1[i].copyOf()
            } else {
                child[i] = parent2[i].copyOf()
            }
        }

        return child
    }

    /**
     * Iterates over the pool and applies the action to each genome.
     *
     */
    fun forEachProvider(action: (indexInPool: Int, GeneticNeuronProvider) -> Unit) {
        for (i in 0 until poolSize) {
            val genome = pool[i]
            val provider = GeneticNeuronProvider(genome)
            action(i, provider)
        }
    }

    fun getRandomProvider(): GeneticNeuronProvider {
        val topScoringIndices = fitnessScores.indices
            .sortedByDescending { fitnessScores[it] }
            .take(poolSize / 10) // Adjust the fraction as needed
        val randomIndex = topScoringIndices.random()
        return GeneticNeuronProvider(pool[randomIndex])
    }

    fun setFitness(index: Int, fitness: Double) {
        require(index in 0 until poolSize) { "Index out of bounds" }
        require(fitness >= 0) { "Fitness must be non-negative" }
        fitnessScores = fitnessScores.toMutableList().apply { set(index, fitness) }
    }

    fun evolve(generationSize: Int, eliteSize: Int = 2, newGeneProbability: Double = 0.1) {
        require(eliteSize < generationSize) { "Elite size must be less than generation size" }

        // Create new generation
        val newPool = mutableListOf<Array<LongArray>>()

        // Keep elite genomes
        val eliteIndices = fitnessScores.indices
            .filter { !fitnessScores[it].isNaN() } // Exclude NaN values
            .sortedByDescending { fitnessScores[it] }
            .take(eliteSize)
        newPool.addAll(eliteIndices.map { pool[it] })

        // Generate rest of new generation through crossover, mutation, and new genes
        while (newPool.size < generationSize) {
            val child = when {
                Random.nextDouble() < newGeneProbability -> {
                    // Create a completely new random gene
                    generateGenome()
                }
                else -> {
                    // Tournament selection
                    val parent1Index = tournamentSelect(eliteSize)
                    val parent2Index = tournamentSelect(eliteSize, exludingIndex = parent1Index)

                    // Create child through crossover
                    val offspring = crossover(parent1Index, parent2Index)

                    // Mutate child with some probability (except for completely new genes)
                    mutateGenome(offspring)
                    offspring
                }
            }

            newPool.add(child)
        }

        // Update pool and reset fitness scores
        pool = newPool
        fitnessScores = List(generationSize) { 0.0 }
    }

    private fun tournamentSelect(tournamentSize: Int = 3, exludingIndex: Int? = null): Int {
        val tournament = (0 until poolSize)
            .filter { it != exludingIndex }
            .shuffled()
            .take(tournamentSize)
        return tournament.maxByOrNull { fitnessScores[it] } ?: tournament.first()
    }

    private fun mutateGenome(genome: Array<LongArray>) {
        for (i in genome.indices) {
            for (j in 0 until GeneticNeuronProvider.GENES_PER_NEURON) {
                var gene = genome[i][j]
                for (bit in 0 until 64) {
                    if (Random.nextDouble() < mutationRate) {
                        gene = gene xor (1L shl bit)
                    }
                }
                genome[i][j] = gene
            }
        }
    }

    fun reinitialize() {
        pool = List(poolSize) { generateGenome() }
        fitnessScores = List(poolSize) { 0.0 }
    }
} 