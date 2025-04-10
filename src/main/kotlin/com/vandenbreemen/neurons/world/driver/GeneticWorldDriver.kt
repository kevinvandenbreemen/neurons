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
    existingGenePool: GeneticPool? = null
) {

    private val randomWorlds = MutableList(numWorlds) {
        World.randomWorld(
            width = worldWidth,
            height = worldHeight,
            wallDensity = wallDensity,
            minRoomSize = 8,
            maxRoomSize = 20,
            numRooms = 2,
            numRandomWalls = 2
        )
    }

    private val genePool = existingGenePool ?: GeneticPool(
        brainSizeX, brainSizeY, numGenes, mutationRate
    )

    fun getGenePool(): GeneticPool {
        return genePool
    }

    fun drive(
        fitnessFunction: (geneticNeuronProvider: GeneticNeuronProvider, numMoves: Int) -> Double,
        onEpochComplete: (Int) -> Unit = {}
    ) {
        for (i in 0 until numEpochs) {
            iterate(numMovesPerTest) { geneticNeuronProvider, numMoves ->
                fitnessFunction(geneticNeuronProvider, numMoves)
            }
            genePool.evolve(numGenes, eliteSize)
            onEpochComplete(i + 1)
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


    fun getFitness(geneticNeuronProvider: GeneticNeuronProvider, numMoves: Int): Double {
        var numWallHitCount = 0.0
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

            if (simulation.isAgentOnWall(agent)) {
                numWallHitCount++
            }
            if (simulation.isAgentOutOfBounds(agent)) {  //  Going out of bounds is right off
                return 0.0
            }

        }

        val score = if (didAgentMove) (
                max((numMoves.toDouble() - numWallHitCount - numIterationWithoutMovement), 0.0)
                        / numMoves) else 0.0

        return score
    }

}

fun main() {
    val driver = GeneticWorldDriver(
        numWorlds = 10,
        brainSizeX = 10,
        brainSizeY = 10,
        numGenes = 100,
        numMovesPerTest = 1000,
    )

    driver.drive(
        fitnessFunction = { geneticNeuronProvider, numMoves ->
            driver.getFitness(geneticNeuronProvider, numMoves)
        }
    )
}