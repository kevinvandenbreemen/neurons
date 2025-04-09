package com.vandenbreemen.neurons.evolution

import com.vandenbreemen.neurons.provider.GeneticNeuronProvider
import kotlin.random.Random

class GeneticPool(
    private val rows: Int,
    private val cols: Int,
    private val poolSize: Int
) {
    private var pool: List<LongArray> = List(poolSize) { generateGenome() }
    private var fitnessScores: List<Double> = List(poolSize) { 0.0 }

    private fun generateGenome(): LongArray {
        val geneList = LongArray(rows * cols)
        for (i in 0 until rows * cols) {
            geneList[i] = Random.nextLong()
        }
        return geneList
    }

    fun getGenome(index: Int): LongArray {
        require(index in 0 until poolSize) { "Index out of bounds" }
        return pool[index]
    }

    fun getAllGenomes(): List<LongArray> = pool

    fun mutateGenome(index: Int, mutationRate: Double = 0.1) {
        require(index in 0 until poolSize) { "Index out of bounds" }
        require(mutationRate in 0.0..1.0) { "Mutation rate must be between 0 and 1" }

        val genome = pool[index].copyOf()
        for (i in genome.indices) {
            if (Random.nextDouble() < mutationRate) {
                // Flip a random bit in the long value
                val bitPosition = Random.nextInt(64)
                genome[i] = genome[i] xor (1L shl bitPosition)
            }
        }
        pool = pool.toMutableList().apply { set(index, genome) }
    }

    fun crossover(parent1Index: Int, parent2Index: Int): LongArray {
        require(parent1Index in 0 until poolSize && parent2Index in 0 until poolSize) { "Parent indices out of bounds" }

        val parent1 = pool[parent1Index]
        val parent2 = pool[parent2Index]
        val child = LongArray(parent1.size)

        // Perform single-point crossover
        val crossoverPoint = Random.nextInt(parent1.size)
        for (i in child.indices) {
            child[i] = if (i < crossoverPoint) parent1[i] else parent2[i]
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
        val randomIndex = Random.nextInt(poolSize)
        return GeneticNeuronProvider(pool[randomIndex])
    }

    fun setFitness(index: Int, fitness: Double) {
        require(index in 0 until poolSize) { "Index out of bounds" }
        require(fitness >= 0) { "Fitness must be non-negative" }
        fitnessScores = fitnessScores.toMutableList().apply { set(index, fitness) }
    }

    fun evolve(generationSize: Int, eliteSize: Int = 2) {
        require(eliteSize < generationSize) { "Elite size must be less than generation size" }

        // Create new generation
        val newPool = mutableListOf<LongArray>()

        // Keep elite genomes
        val eliteIndices = fitnessScores.indices
            .sortedByDescending { fitnessScores[it] }
            .take(eliteSize)

        newPool.addAll(eliteIndices.map { pool[it] })

        // Generate rest of new generation through crossover and mutation
        while (newPool.size < generationSize) {
            // Tournament selection
            val parent1Index = tournamentSelect()
            val parent2Index = tournamentSelect()

            // Create child through crossover
            val child = crossover(parent1Index, parent2Index)

            // Mutate child with some probability
            if (Random.nextDouble() < 0.1) { // 10% mutation rate
                mutateGenome(child)
            }

            newPool.add(child)
        }

        // Update pool and reset fitness scores
        pool = newPool
        fitnessScores = List(generationSize) { 0.0 }
    }

    private fun tournamentSelect(tournamentSize: Int = 3): Int {
        val tournament = (0 until poolSize).shuffled().take(tournamentSize)
        return tournament.maxByOrNull { fitnessScores[it] } ?: tournament.first()
    }

    private fun mutateGenome(genome: LongArray) {
        val mutationPoint = Random.nextInt(genome.size)
        val bitPosition = Random.nextInt(64)
        genome[mutationPoint] = genome[mutationPoint] xor (1L shl bitPosition)
    }

    companion object {
        fun create(rows: Int, cols: Int, poolSize: Int): GeneticPool {
            return GeneticPool(rows, cols, poolSize)
        }
    }
} 