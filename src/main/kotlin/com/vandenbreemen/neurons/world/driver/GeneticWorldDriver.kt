package com.vandenbreemen.neurons.world.driver

import com.vandenbreemen.neurons.agent.NeuralAgent
import com.vandenbreemen.neurons.evolution.GeneticPool
import com.vandenbreemen.neurons.model.NeuralNet
import com.vandenbreemen.neurons.provider.GeneticNeuronProvider
import com.vandenbreemen.neurons.world.controller.NavigationWorldSimulation
import com.vandenbreemen.neurons.world.model.World
import kotlin.math.max

class GeneticWorldDriver(
    numWorlds: Int,
    private val brainSizeX: Int,
    private val brainSizeY: Int,
    private val numGenes: Int,
    private val numMovesPerTest: Int = 100,
    private val costOfNotMoving: Double = 0.1,
    private val mutationRate: Double = 0.1,
    private val eliteSize: Int = 5,
    private val learningRate: Double = 0.1,
    private val worldWidth: Int = 100,
    private val worldHeight: Int = 100,
    private val wallDensity: Double = 0.001,
    private val numEpochs: Int = 10,
    private val numRooms: Int = 2,
    private val numRandomWalls: Int = 2,
    private val newGeneProbability: Double = 0.1,
    existingGenePool: GeneticPool? = null
) {

    private val randomWorlds = MutableList(numWorlds) {
        World.randomWorld(
            width = worldWidth,
            height = worldHeight,
            wallDensity = wallDensity,
            minRoomSize = 8,
            maxRoomSize = 20,
            numRooms = numRooms,
            numRandomWalls = numRandomWalls
        )
    }

    private val genePool = existingGenePool ?: GeneticPool(
        brainSizeX, brainSizeY, numGenes, mutationRate
    )

    fun getGenePool(): GeneticPool {
        return genePool
    }

    fun getRandomWorld(): World {
        return randomWorlds.random()
    }

    fun drive(
        fitnessFunction: (geneticNeuronProvider: GeneticNeuronProvider, numMoves: Int) -> Double,
        onEpochComplete: (Int, Double) -> Unit = { _, _ -> },
        numWorldsToTest: Int = 1
    ) {
        var bestScore = 0.0
        for (i in 0 until numEpochs) {
            val score = iterate(numMovesPerTest, numWorldsToTest) { geneticNeuronProvider, numMoves ->
                fitnessFunction(geneticNeuronProvider, numMoves)
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

    /**
     * Creates a NavigationWorldSimulation with a randomly selected world and a single neural agent.

     */
    fun createSimulationWithAgent(): Pair<NavigationWorldSimulation, NeuralNet> {
        val world = randomWorlds.random()
        val neuralNet = getRandomNeuralNetwork()
        val agent = NeuralAgent(neuralNet, learningRate)
        return Pair(NavigationWorldSimulation(world).apply {
            addAgent(agent, world.getRandomEmptyCell())
        }, neuralNet)
    }

    private fun iterate(
        numMoves: Int,
        numWorldsToTest: Int = 1,
        fitnessFunction: (geneticNeuronProvider: GeneticNeuronProvider, numMoves: Int) -> Double
    ): Double {

        var bestScore = 0.0

        genePool.forEachProvider { indexInPool, geneticNeuronProvider ->
            val score = fitnessFunction(geneticNeuronProvider, numMoves)
            if (score > bestScore) {
                bestScore = score
            }
            genePool.setFitness(indexInPool, score)
        }

        return bestScore
    }


    fun getFitness(
        geneticNeuronProvider: GeneticNeuronProvider,
        numMoves: Int,
        numWorldsToTest: Int = 1,
        painTolerance: Double = 5.0,
    ): Double {
        var totalScore = 0.0

        // Test the neural network on multiple random worlds
        for (worldIndex in 0 until numWorldsToTest) {
            var numIterationWithoutMovement = 0.0
            var didAgentMove = false

            val world = randomWorlds.random()
            val neuralNet = NeuralNet(
                brainSizeX,
                brainSizeY,
                geneticNeuronProvider
            )
            val agent = NeuralAgent(neuralNet, learningRate)
            val simulation = NavigationWorldSimulation(world).also {
                it.addAgent(
                    agent,
                    world.getRandomEmptyCell()
                )
            }

            for (i in 0 until numMoves) {
                val currentAgentPos = simulation.getAgentPosition(agent)
                simulation.step()
                if (simulation.getAgentPosition(agent) == currentAgentPos) {
                    numIterationWithoutMovement += costOfNotMoving
                } else {
                    didAgentMove = true
                }

                if (simulation.isAgentOutOfBounds(agent)) {  //  Going out of bounds is right off
                    return 0.0
                }

                if ((numMoves.toDouble() - numIterationWithoutMovement) <= 0) {
                    return 0.0
                }

                if (simulation.getPainAmount() > painTolerance) {
                    return 0.0
                }

            }

            val score = if (didAgentMove) (
                    max((numMoves.toDouble() - simulation.getPainAmount() - numIterationWithoutMovement), 0.0)
                            / numMoves) else 0.0

            totalScore += score
        }

        // Return the average score across all tested worlds
        return totalScore / numWorldsToTest
    }

}

fun main() {
    val driver = GeneticWorldDriver(
        numWorlds = 10,
        brainSizeX = 10,
        brainSizeY = 10,
        numGenes = 100,
        numMovesPerTest = 1000,
        costOfNotMoving = 0.1,
        mutationRate = 0.1,
        eliteSize = 5,
        learningRate = 0.1,
        worldWidth = 100,
        worldHeight = 100,
        wallDensity = 0.001,
        numEpochs = 10,
        numRooms = 2,
        numRandomWalls = 2,
    )

    driver.drive(
        fitnessFunction = { geneticNeuronProvider, numMoves ->
            driver.getFitness(geneticNeuronProvider, numMoves)
        }
    )
}