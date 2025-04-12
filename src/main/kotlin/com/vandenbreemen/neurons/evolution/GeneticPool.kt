package com.vandenbreemen.neurons.evolution

import com.vandenbreemen.neurons.provider.GeneticNeuronProvider
import kotlin.random.Random

class GeneticPool(
    private val rows: Int,
    private val cols: Int,
    private val poolSize: Int,
    private val mutationRate: Double = 0.1,
    private val pruningRate: Double = 0.05
) {

    init {
        println("Pruning rate = $pruningRate")
    }

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

    private fun prune(genome: LongArray): LongArray {
        val prunedGenome = genome.copyOf()
        for (i in prunedGenome.indices) {
            if (Random.nextDouble() < pruningRate) {
                // Get the current gene
                var gene = prunedGenome[i]

                // Clear bits 4-7 (neuron type bits)
                // 0xF0 is 11110000 in binary, we want to clear these bits
                val mask = 0xF0L
                gene = gene and mask.inv()

                // Set neuron type to 1 (DeadNeuron)
                gene = gene or (1L shl 4)

                // Update the gene in the genome
                prunedGenome[i] = gene
            }
        }
        return prunedGenome
    }

    fun evolve(generationSize: Int, eliteSize: Int = 2, newGeneProbability: Double = 0.1) {
        require(eliteSize < generationSize) { "Elite size must be less than generation size" }

        // Create new generation
        val newPool = mutableListOf<LongArray>()

        // Keep elite genomes
        val eliteIndices = fitnessScores.indices
            .filter { !fitnessScores[it].isNaN() } // Exclude NaN values
            .sortedByDescending { fitnessScores[it] }
            .take(eliteSize)
        newPool.addAll(eliteIndices.map { pool[it] })

        val prunableIndices = fitnessScores.indices
            .filter { !eliteIndices.contains(it) }
            .take(eliteSize)
        newPool.addAll(prunableIndices.map { prune(pool[it]) })

        // Generate rest of new generation through crossover, mutation, and new genes
        while (newPool.size < generationSize) {
            val child = when {
                Random.nextDouble() < newGeneProbability -> {
                    // Create a completely new random gene
                    generateGenome()
                }

                else -> {
                    // Tournament selection
                    val parent1Index = tournamentSelect()
                    val parent2Index = tournamentSelect()

                    // Create child through crossover
                    val offspring = crossover(parent1Index, parent2Index)

                    // Mutate child with some probability (except for completely new genes)
                    return mutateGenome(offspring)
                }
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
        for (i in genome.indices) {
            var gene = genome[i]
            for (bit in 0 until 64) {
                if (Random.nextDouble() < mutationRate) {
                    gene = gene xor (1L shl bit)
                }
            }
            genome[i] = gene
        }
    }

    fun reinitialize() {
        pool = List(poolSize) { generateGenome() }
        fitnessScores = List(poolSize) { 0.0 }
    }

    companion object {
        fun create(
            rows: Int,
            cols: Int,
            poolSize: Int,
            mutationRate: Double = 0.1,
            pruningRate: Double = 0.05
        ): GeneticPool {
            return GeneticPool(rows, cols, poolSize, mutationRate, pruningRate)
        }
    }
} 