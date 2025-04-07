package com.vandenbreemen.neurons.world.controller

import com.vandenbreemen.neurons.agent.NeuralAgent
import com.vandenbreemen.neurons.world.model.World

class NavigationWorldSimulation(
    world: World = World(),
) : WorldSimulation(world) {

    override fun doAgentSetup(agent: NeuralAgent) {
        // North
        agent.findMotorNeurons { idByte -> idByte in 0x00..0x1F }.forEach { neuron ->
            agent.addNeuronAction(neuron) {
                if (neuron.activation > 0.5) {
                    val currentPos = getAgentPosition(agent) ?: return@addNeuronAction
                    val newPos = currentPos.copy(y = currentPos.y - 1)
                    if (!world.isWall(newPos.x, newPos.y)) {
                        setAgentPosition(agent, newPos)
                    }
                }
            }
        }

        // Northeast
        agent.findMotorNeurons { idByte -> idByte in 0x20..0x3F }.forEach { neuron ->
            agent.addNeuronAction(neuron) {
                if (neuron.activation > 0.5) {
                    val currentPos = getAgentPosition(agent) ?: return@addNeuronAction
                    val newPos = currentPos.copy(x = currentPos.x + 1, y = currentPos.y - 1)
                    if (!world.isWall(newPos.x, newPos.y)) {
                        setAgentPosition(agent, newPos)
                    }
                }
            }
        }

        // East
        agent.findMotorNeurons { idByte -> idByte in 0x40..0x5F }.forEach { neuron ->
            agent.addNeuronAction(neuron) {
                if (neuron.activation > 0.5) {
                    val currentPos = getAgentPosition(agent) ?: return@addNeuronAction
                    val newPos = currentPos.copy(x = currentPos.x + 1)
                    if (!world.isWall(newPos.x, newPos.y)) {
                        setAgentPosition(agent, newPos)
                    }
                }
            }
        }

        // Southeast
        agent.findMotorNeurons { idByte -> idByte in 0x60..0x7F }.forEach { neuron ->
            agent.addNeuronAction(neuron) {
                if (neuron.activation > 0.5) {
                    val currentPos = getAgentPosition(agent) ?: return@addNeuronAction
                    val newPos = currentPos.copy(x = currentPos.x + 1, y = currentPos.y + 1)
                    if (!world.isWall(newPos.x, newPos.y)) {
                        setAgentPosition(agent, newPos)
                    }
                }
            }
        }

        // South
        agent.findMotorNeurons { idByte -> idByte in 0x80..0x9F }.forEach { neuron ->
            agent.addNeuronAction(neuron) {
                if (neuron.activation > 0.5) {
                    val currentPos = getAgentPosition(agent) ?: return@addNeuronAction
                    val newPos = currentPos.copy(y = currentPos.y + 1)
                    if (!world.isWall(newPos.x, newPos.y)) {
                        setAgentPosition(agent, newPos)
                    }
                }
            }
        }

        // Southwest
        agent.findMotorNeurons { idByte -> idByte in 0xA0..0xBF }.forEach { neuron ->
            agent.addNeuronAction(neuron) {
                if (neuron.activation > 0.5) {
                    val currentPos = getAgentPosition(agent) ?: return@addNeuronAction
                    val newPos = currentPos.copy(x = currentPos.x - 1, y = currentPos.y + 1)
                    if (!world.isWall(newPos.x, newPos.y)) {
                        setAgentPosition(agent, newPos)
                    }
                }
            }
        }

        // West
        agent.findMotorNeurons { idByte -> idByte in 0xC0..0xDF }.forEach { neuron ->
            agent.addNeuronAction(neuron) {
                if (neuron.activation > 0.5) {
                    val currentPos = getAgentPosition(agent) ?: return@addNeuronAction
                    val newPos = currentPos.copy(x = currentPos.x - 1)
                    if (!world.isWall(newPos.x, newPos.y)) {
                        setAgentPosition(agent, newPos)
                    }
                }
            }
        }

        // Northwest
        agent.findMotorNeurons { idByte -> idByte in 0xE0..0xFF }.forEach { neuron ->
            agent.addNeuronAction(neuron) {
                if (neuron.activation > 0.5) {
                    val currentPos = getAgentPosition(agent) ?: return@addNeuronAction
                    val newPos = currentPos.copy(x = currentPos.x - 1, y = currentPos.y - 1)
                    if (!world.isWall(newPos.x, newPos.y)) {
                        setAgentPosition(agent, newPos)
                    }
                }
            }
        }
    }

}