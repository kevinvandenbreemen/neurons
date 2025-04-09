package com.vandenbreemen.neurons.world.driver

import com.vandenbreemen.neurons.evolution.GeneticPool
import com.vandenbreemen.neurons.world.model.World

class GeneticWorldDriver(
    numWorlds: Int,
    brainSizeX: Int,
    brainSizeY: Int,
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


}