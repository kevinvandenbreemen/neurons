package com.vandenbreemen.neurons.world.controller

import com.vandenbreemen.neurons.agent.NeuralAgent
import com.vandenbreemen.neurons.world.model.World

class NavigationWorldSimulation(
    world: World = World(),
) : WorldSimulation(world) {

    override fun doAgentSetup(agent: NeuralAgent) {
        motorNeuronSetup(agent)
        sensoryNeuronSetup(agent)
    }

    private fun motorNeuronSetup(agent: NeuralAgent) {
        // North
        agent.findMotorNeurons { idByte -> idByte in 0x00..0x1F }.forEach { neuron ->
            agent.addNeuronAction(neuron) {
                if (neuron.activation > 0.5) {
                    val currentPos = getAgentPosition(agent) ?: return@addNeuronAction
                    val newPos = currentPos.copy(y = currentPos.y - 1)
                    setAgentPosition(agent, newPos)
                }
            }
        }

        // Northeast
        agent.findMotorNeurons { idByte -> idByte in 0x20..0x3F }.forEach { neuron ->
            agent.addNeuronAction(neuron) {
                if (neuron.activation > 0.5) {
                    val currentPos = getAgentPosition(agent) ?: return@addNeuronAction
                    val newPos = currentPos.copy(x = currentPos.x + 1, y = currentPos.y - 1)
                    setAgentPosition(agent, newPos)
                }
            }
        }

        // East
        agent.findMotorNeurons { idByte -> idByte in 0x40..0x5F }.forEach { neuron ->
            agent.addNeuronAction(neuron) {
                if (neuron.activation > 0.5) {
                    val currentPos = getAgentPosition(agent) ?: return@addNeuronAction
                    val newPos = currentPos.copy(x = currentPos.x + 1)
                    setAgentPosition(agent, newPos)
                }
            }
        }

        // Southeast
        agent.findMotorNeurons { idByte -> idByte in 0x60..0x7F }.forEach { neuron ->
            agent.addNeuronAction(neuron) {
                if (neuron.activation > 0.5) {
                    val currentPos = getAgentPosition(agent) ?: return@addNeuronAction
                    val newPos = currentPos.copy(x = currentPos.x + 1, y = currentPos.y + 1)
                    setAgentPosition(agent, newPos)
                }
            }
        }

        // South
        agent.findMotorNeurons { idByte -> idByte in 0x80..0x9F }.forEach { neuron ->
            agent.addNeuronAction(neuron) {
                if (neuron.activation > 0.5) {
                    val currentPos = getAgentPosition(agent) ?: return@addNeuronAction
                    val newPos = currentPos.copy(y = currentPos.y + 1)
                    setAgentPosition(agent, newPos)
                }
            }
        }

        // Southwest
        agent.findMotorNeurons { idByte -> idByte in 0xA0..0xBF }.forEach { neuron ->
            agent.addNeuronAction(neuron) {
                if (neuron.activation > 0.5) {
                    val currentPos = getAgentPosition(agent) ?: return@addNeuronAction
                    val newPos = currentPos.copy(x = currentPos.x - 1, y = currentPos.y + 1)
                    setAgentPosition(agent, newPos)
                }
            }
        }

        // West
        agent.findMotorNeurons { idByte -> idByte in 0xC0..0xDF }.forEach { neuron ->
            agent.addNeuronAction(neuron) {
                if (neuron.activation > 0.5) {
                    val currentPos = getAgentPosition(agent) ?: return@addNeuronAction
                    val newPos = currentPos.copy(x = currentPos.x - 1)
                    setAgentPosition(agent, newPos)
                }
            }
        }

        // Northwest
        agent.findMotorNeurons { idByte -> idByte in 0xE0..0xFF }.forEach { neuron ->
            agent.addNeuronAction(neuron) {
                if (neuron.activation > 0.5) {
                    val currentPos = getAgentPosition(agent) ?: return@addNeuronAction
                    val newPos = currentPos.copy(x = currentPos.x - 1, y = currentPos.y - 1)
                    setAgentPosition(agent, newPos)
                }
            }
        }
    }

    private fun sensoryNeuronSetup(agent: NeuralAgent) {
        // North
        agent.findSensoryNeurons { idByte -> idByte in 0x00..0x1F }.forEach { neuron ->
            // Sensory neuron for detecting walls to the north
            neuron.stimulate(if (isWallInDirection(agent, 0, -1)) 1.0 else 0.0)
        }

        // Northeast
        agent.findSensoryNeurons { idByte -> idByte in 0x20..0x3F }.forEach { neuron ->
            // Sensory neuron for detecting walls to the northeast
            neuron.stimulate(if (isWallInDirection(agent, 1, -1)) 1.0 else 0.0)
        }

        // East
        agent.findSensoryNeurons { idByte -> idByte in 0x40..0x5F }.forEach { neuron ->
            // Sensory neuron for detecting walls to the east
            neuron.stimulate(if (isWallInDirection(agent, 1, 0)) 1.0 else 0.0)
        }

        // Southeast
        agent.findSensoryNeurons { idByte -> idByte in 0x60..0x7F }.forEach { neuron ->
            // Sensory neuron for detecting walls to the southeast
            neuron.stimulate(if (isWallInDirection(agent, 1, 1)) 1.0 else 0.0)
        }

        // South
        agent.findSensoryNeurons { idByte -> idByte in 0x80..0x9F }.forEach { neuron ->
            // Sensory neuron for detecting walls to the south
            neuron.stimulate(if (isWallInDirection(agent, 0, 1)) 1.0 else 0.0)
        }

        // Southwest
        agent.findSensoryNeurons { idByte -> idByte in 0xA0..0xBF }.forEach { neuron ->
            // Sensory neuron for detecting walls to the southwest
            neuron.stimulate(if (isWallInDirection(agent, -1, 1)) 1.0 else 0.0)
        }

        // West
        agent.findSensoryNeurons { idByte -> idByte in 0xC0..0xDF }.forEach { neuron ->
            // Sensory neuron for detecting walls to the west
            neuron.stimulate(if (isWallInDirection(agent, -1, 0)) 1.0 else 0.0)
        }

        // Northwest
        agent.findSensoryNeurons { idByte -> idByte in 0xE0..0xFF }.forEach { neuron ->
            // Sensory neuron for detecting walls to the northwest
            neuron.stimulate(if (isWallInDirection(agent, -1, -1)) 1.0 else 0.0)
        }
    }

    /**
     * Checks if there is a wall in a specified direction relative to an agent's current position
     * @param agent The neural agent whose position will be used as the reference point
     * @param dx The change in x-coordinate (-1 for west, 0 for no change, 1 for east)
     * @param dy The change in y-coordinate (-1 for north, 0 for no change, 1 for south)
     * @return true if there is a wall in the specified direction, false otherwise or if agent position is unknown
     */
    private fun isWallInDirection(agent: NeuralAgent, dx: Int, dy: Int): Boolean {
        val currentPos = getAgentPosition(agent) ?: return false
        val newPos = currentPos.copy(x = currentPos.x + dx, y = currentPos.y + dy)
        return world.isWall(newPos.x, newPos.y)
    }

    /**
     * Checks if the agent is on a wall
     */
    fun isAgentOnWall(agent: NeuralAgent): Boolean {
        val currentPos = getAgentPosition(agent) ?: return false
        return world.isWall(currentPos.x, currentPos.y)
    }

}