package com.vandenbreemen.neurons.world.driver

import com.vandenbreemen.neurons.agent.NeuralAgent
import com.vandenbreemen.neurons.evolution.GeneticPool
import com.vandenbreemen.neurons.model.NeuralNet
import com.vandenbreemen.neurons.world.controller.NavigationWorldSimulation
import com.vandenbreemen.neurons.world.model.World

class GeneticWorldDriver(
    numWorlds: Int,
    private val brainSizeX: Int,
    private val brainSizeY: Int,
    numGenes: Int
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

        val numMoves = 100

        genePool.forEachProvider { indexInPool, geneticNeuronProvider ->

            var numWallHitCount = 0.0

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
                simulation.step()
                if (simulation.isAgentOnWall(agent)) {
                    numWallHitCount++
                }

            }

            val score = ((numMoves.toDouble() - numWallHitCount) / numMoves).toDouble()
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