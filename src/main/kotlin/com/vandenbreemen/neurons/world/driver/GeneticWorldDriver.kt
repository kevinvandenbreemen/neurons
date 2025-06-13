package com.vandenbreemen.neurons.world.driver

import com.vandenbreemen.neurons.agent.NeuralAgent
import com.vandenbreemen.neurons.evolution.fitness.GeneticFitnessDriver
import com.vandenbreemen.neurons.evolution.model.GeneticPool
import com.vandenbreemen.neurons.model.NeuralNet
import com.vandenbreemen.neurons.provider.GeneticNeuronProvider
import com.vandenbreemen.neurons.world.controller.NavigationWorldSimulation
import com.vandenbreemen.neurons.world.model.AgentPosition
import com.vandenbreemen.neurons.world.model.World
import kotlin.math.max

class GeneticWorldDriver(
    numWorlds: Int,
    private val brainSizeX: Int,
    private val brainSizeY: Int,
    private val numGenes: Int,
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
) : GeneticFitnessDriver(
    brainSizeX = brainSizeX,
    brainSizeY = brainSizeY,
    numGenes = numGenes,
    mutationRate = mutationRate,
    eliteSize = eliteSize,
    numEpochs = numEpochs,
    newGeneProbability = newGeneProbability,
    existingGenePool = existingGenePool
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

    fun getRandomWorld(): World {
        return randomWorlds.random()
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


    fun getFitness(
        geneticNeuronProvider: GeneticNeuronProvider,
        numMoves: Int,
        numWorldsToTest: Int = 1,
        painTolerance: Double = 5.0,
        minViability: Double = 0.01,
        minDistinctPointsInPath: Int = 10,
    ): Double {
        var totalScore = 0.0

        val pointsVisitedPath = mutableListOf<AgentPosition>()

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

                if (pointsVisitedPath.size > minDistinctPointsInPath) {
                    pointsVisitedPath.removeFirst()
                }

                simulation.getAgentPosition(agent)?.let {
                    pointsVisitedPath.add(it)
                }

                simulation.step()
                if (pointsVisitedPath.contains(simulation.getAgentPosition(agent))) {
                    numIterationWithoutMovement += costOfNotMoving
                } else {
                    didAgentMove = true
                }

                if ((numMoves.toDouble() - numIterationWithoutMovement) <= 0) {
                    return 0.0
                }

                if (simulation.getPainAmount() > painTolerance) {
                    return 0.0
                }

                //  Don't consider anything viable if it drops below 50% of the moves
                if (max(
                        (numMoves.toDouble() - simulation.getPainAmount() - numIterationWithoutMovement),
                        0.0
                    ) < minViability
                ) {
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
        numWorlds = 100,
        brainSizeX = 10,
        brainSizeY = 10,
        numGenes = 100,
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
        fitnessFunction = {
            driver.getFitness(
                geneticNeuronProvider = it,
                numMoves = 1000,
                numWorldsToTest = 10,
                painTolerance = 5.0,
                minViability = 0.1,
                minDistinctPointsInPath = 10
            )
        }
    )
}