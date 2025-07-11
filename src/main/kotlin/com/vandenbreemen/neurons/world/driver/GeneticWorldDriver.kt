package com.vandenbreemen.neurons.world.driver

import com.vandenbreemen.neurons.agent.NeuralAgent
import com.vandenbreemen.neurons.evolution.fitness.GeneticFitnessDriver
import com.vandenbreemen.neurons.evolution.model.GeneticPool
import com.vandenbreemen.neurons.model.NeuralNet
import com.vandenbreemen.neurons.provider.GeneticNeuronProvider
import com.vandenbreemen.neurons.world.controller.NavigationWorldSimulation
import com.vandenbreemen.neurons.world.model.AgentPosition
import com.vandenbreemen.neurons.world.model.World
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlin.math.max

class GeneticWorldDriver(
    numWorlds: Int,
    private val brainSizeX: Int,
    private val brainSizeY: Int,
    private val numGenes: Int,
    private val costOfNotMoving: Double = 0.1,
    private val mutationRate: Double = 0.1,
    private val eliteSize: Int = 5,
    private val worldWidth: Int = 100,
    private val worldHeight: Int = 100,
    private val wallDensity: Double = 0.001,
    private val numEpochs: Int = 10,
    private val numRooms: Int = 2,
    private val numRandomWalls: Int = 2,
    private val newGeneProbability: Double = 0.1,
    private val errorWeight: Double = 1.0,
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
        val agent = NeuralAgent(neuralNet)
        return Pair(NavigationWorldSimulation(world).apply {
            addAgent(agent, world.getRandomEmptyCell())
        }, neuralNet)
    }


    fun getFitness(
        geneticNeuronProvider: GeneticNeuronProvider,
        numMoves: Int,
        numWorldsToTest: Int = 1,
        minScore: Double = 0.1,
    ): Double = runBlocking {
        val scores = List(numWorldsToTest) { worldIndex ->
            async(Dispatchers.Default) {
                var numIterationWithoutMovement = 0.0
                var didAgentMove = false

                // Create a fresh provider for this world test
                val freshProvider = geneticNeuronProvider.copy()

                val world = randomWorlds.random()
                val neuralNet = NeuralNet(
                    brainSizeX,
                    brainSizeY,
                    freshProvider
                )
                val agent = NeuralAgent(neuralNet)
                val simulation = NavigationWorldSimulation(world).also {
                    it.addAgent(
                        agent,
                        world.getRandomEmptyCell()
                    )
                }

                val pointsVisitedPath = mutableListOf<AgentPosition>()
                val minDistinctPointsInPath = (numMoves * 0.5).toInt()

                for (i in 0 until numMoves) {
                    if (pointsVisitedPath.size > minDistinctPointsInPath) {
                        pointsVisitedPath.removeFirst()
                    }

                    simulation.getAgentPosition(agent)?.let {
                        pointsVisitedPath.add(it)
                    }

                    simulation.step()
                    if (pointsVisitedPath.contains(simulation.getAgentPosition(agent))) {
                        numIterationWithoutMovement++
                    } else {
                        didAgentMove = true
                    }

                    //  If we get too many errors then we should stop
                    if (1.0 - (simulation.errorCount * errorWeight) / numMoves.toDouble() < minScore) {
                        return@async 0.0
                    }

                    val noMovementProportion = (numIterationWithoutMovement * costOfNotMoving) / numMoves.toDouble()
                    if (1.0 - noMovementProportion < minScore) {
                        return@async 0.0
                    }
                }

                if (didAgentMove) {
                    (max(
                        (numMoves.toDouble() - (numIterationWithoutMovement * costOfNotMoving) - (simulation.errorCount * errorWeight)),
                        0.0
                    ) / numMoves).let {
                        return@let if (it >= minScore) {  //  Don't let crappy genes continue to next iteration
                            it
                        } else {
                            0.0
                        }
                    }
                } else {
                    0.0
                }
            }
        }.awaitAll()

        return@runBlocking scores.average().let {
            if (it >= minScore) {  //  Don't let crappy genes continue to next iteration
                return@let it
            } else {
                return@let 0.0
            }
        }
    }

}