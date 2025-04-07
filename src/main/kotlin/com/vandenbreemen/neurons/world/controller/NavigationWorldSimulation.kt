package com.vandenbreemen.neurons.world.controller

import com.vandenbreemen.neurons.agent.NeuralAgent
import com.vandenbreemen.neurons.world.model.World

class NavigationWorldSimulation(
    world: World = World(),
) : WorldSimulation(world) {

    override fun doAgentSetup(agent: NeuralAgent) {
        //  Up neurons
        agent.findMotorNeurons { idByte -> idByte in 0x00..0x1F }.apply {
            // North
        }

        agent.findMotorNeurons { idByte -> idByte in 0x20..0x3F }.apply {
            // Northeast
        }

        agent.findMotorNeurons { idByte -> idByte in 0x40..0x5F }.apply {
            // East
        }

        agent.findMotorNeurons { idByte -> idByte in 0x60..0x7F }.apply {
            // Southeast
        }

        agent.findMotorNeurons { idByte -> idByte in 0x80..0x9F }.apply {
            // South
        }

        agent.findMotorNeurons { idByte -> idByte in 0xA0..0xBF }.apply {
            // Southwest
        }

        agent.findMotorNeurons { idByte -> idByte in 0xC0..0xDF }.apply {
            // West
        }

        agent.findMotorNeurons { idByte -> idByte in 0xE0..0xFF }.apply {
            // Northwest
        }

    }

}