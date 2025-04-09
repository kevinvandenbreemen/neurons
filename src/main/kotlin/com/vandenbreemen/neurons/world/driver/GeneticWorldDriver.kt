package com.vandenbreemen.neurons.world.driver

import com.vandenbreemen.neurons.agent.NeuralAgent
import com.vandenbreemen.neurons.evolution.GeneticPool
import com.vandenbreemen.neurons.model.NeuralNet
import com.vandenbreemen.neurons.world.controller.NavigationWorldSimulation
import com.vandenbreemen.neurons.world.model.World
import kotlin.math.max

class GeneticWorldDriver(
    numWorlds: Int,
    private val brainSizeX: Int,
    private val brainSizeY: Int,
    private val numGenes: Int
) {

    private val randomWorlds = MutableList(numWorlds) {
        World.randomWorld(
            width = 100,
            height = 100,
            wallDensity = 0.001,
            minRoomSize = 8,
            maxRoomSize = 20,
            numRooms = 2,
            numRandomWalls = 2
        )
    }


    private val genePool = GeneticPool(
        brainSizeX, brainSizeY, numGenes
    )

    fun drive() {
        for (i in 0 until 10) {
            iterate(100)
            genePool.evolve(numGenes, (numGenes / 4))
        }
    }

    private fun iterate(numMoves: Int) {
        genePool.forEachProvider { indexInPool, geneticNeuronProvider ->

            var numWallHitCount = 0.0
            var numIterationWithoutMovement = 0.0

            val world = randomWorlds.random()
            val neuralNet = NeuralNet(
                brainSizeX,
                brainSizeY,
                geneticNeuronProvider
            )
            val agent = NeuralAgent(neuralNet, 0.1)
            val simulation = NavigationWorldSimulation(world).also {
                it.addAgent(
                    agent
                )
            }

            for (i in 0 until numMoves) {

                val currentAgentPos = simulation.getAgentPosition(agent)
                simulation.step()
                if (simulation.getAgentPosition(agent) == currentAgentPos) {
                    numIterationWithoutMovement++
                }
                if (simulation.isAgentOnWall(agent)) {
                    numWallHitCount++
                }

            }

            val score = (
                    max((numMoves.toDouble() - numWallHitCount - numIterationWithoutMovement), 0.0)
                            / numMoves)
            genePool.setFitness(indexInPool, score)

            println("fitness at index $indexInPool: $score")
        }
    }

}

fun main() {
    val driver = GeneticWorldDriver(
        numWorlds = 10,
        brainSizeX = 10,
        brainSizeY = 10,
        numGenes = 100
    )

    driver.drive()
}