package com.vandenbreemen.neurons.world.controller

import com.vandenbreemen.neurons.agent.NeuralAgent
import com.vandenbreemen.neurons.world.model.World

class NavigationWorldSimulation(
    world: World = World(),
    private val maxMovementDelta: Int = 1
) : WorldSimulation(world) {

    private var painAmount: Double = 0.0

    override fun doAgentSetup(agent: NeuralAgent) {
        motorNeuronSetup(agent)
        sensoryNeuronSetup(agent)
        painReceptorSetup(agent)
    }

    private fun motorNeuronSetup(agent: NeuralAgent) {
        // North
        agent.findMotorNeurons { idByte -> idByte in 0x00..0x1F }.forEach { neuron ->
            agent.addNeuronAction(neuron) {
                val currentPos = getAgentPosition(agent) ?: return@addNeuronAction
                // Map activation from [0, 1] to [0, maxMovementDelta]
                val delta = (neuron.activation * maxMovementDelta).toInt()
                val newPos = currentPos.copy(y = currentPos.y - delta)
                setAgentPosition(agent, newPos)
            }
        }

        // Northeast
        agent.findMotorNeurons { idByte -> idByte in 0x20..0x3F }.forEach { neuron ->
            agent.addNeuronAction(neuron) {
                val currentPos = getAgentPosition(agent) ?: return@addNeuronAction
                val delta = (neuron.activation * maxMovementDelta).toInt()
                val newPos = currentPos.copy(x = currentPos.x + delta, y = currentPos.y - delta)
                setAgentPosition(agent, newPos)
            }
        }

        // East
        agent.findMotorNeurons { idByte -> idByte in 0x40..0x5F }.forEach { neuron ->
            agent.addNeuronAction(neuron) {
                val currentPos = getAgentPosition(agent) ?: return@addNeuronAction
                val delta = (neuron.activation * maxMovementDelta).toInt()
                val newPos = currentPos.copy(x = currentPos.x + delta)
                setAgentPosition(agent, newPos)
            }
        }

        // Southeast
        agent.findMotorNeurons { idByte -> idByte in 0x60..0x7F }.forEach { neuron ->
            agent.addNeuronAction(neuron) {
                val currentPos = getAgentPosition(agent) ?: return@addNeuronAction
                val delta = (neuron.activation * maxMovementDelta).toInt()
                val newPos = currentPos.copy(x = currentPos.x + delta, y = currentPos.y + delta)
                setAgentPosition(agent, newPos)
            }
        }

        // South
        agent.findMotorNeurons { idByte -> idByte in 0x80..0x9F }.forEach { neuron ->
            agent.addNeuronAction(neuron) {
                val currentPos = getAgentPosition(agent) ?: return@addNeuronAction
                val delta = (neuron.activation * maxMovementDelta).toInt()
                val newPos = currentPos.copy(y = currentPos.y + delta)
                setAgentPosition(agent, newPos)
            }
        }

        // Southwest
        agent.findMotorNeurons { idByte -> idByte in 0xA0..0xBF }.forEach { neuron ->
            agent.addNeuronAction(neuron) {
                val currentPos = getAgentPosition(agent) ?: return@addNeuronAction
                val delta = (neuron.activation * maxMovementDelta).toInt()
                val newPos = currentPos.copy(x = currentPos.x - delta, y = currentPos.y + delta)
                setAgentPosition(agent, newPos)
            }
        }

        // West
        agent.findMotorNeurons { idByte -> idByte in 0xC0..0xDF }.forEach { neuron ->
            agent.addNeuronAction(neuron) {
                val currentPos = getAgentPosition(agent) ?: return@addNeuronAction
                val delta = (neuron.activation * maxMovementDelta).toInt()
                val newPos = currentPos.copy(x = currentPos.x - delta)
                setAgentPosition(agent, newPos)
            }
        }

        // Northwest
        agent.findMotorNeurons { idByte -> idByte in 0xE0..0xFF }.forEach { neuron ->
            agent.addNeuronAction(neuron) {
                val currentPos = getAgentPosition(agent) ?: return@addNeuronAction
                val delta = (neuron.activation * maxMovementDelta).toInt()
                val newPos = currentPos.copy(x = currentPos.x - delta, y = currentPos.y - delta)
                setAgentPosition(agent, newPos)
            }
        }
    }

    private fun sensoryNeuronSetup(agent: NeuralAgent) {
        // North
        agent.findSensoryNeurons { idByte -> idByte in 0x00..0x1F }.forEach { neuron ->
            // Sensory neuron for detecting walls to the north
            if (isAgentOutOfBounds(agent)) {
                neuron.stimulateFromEnvironment(1.0)
            } else {
                neuron.stimulateFromEnvironment(getWallDistanceInDirection(agent, 0, -1))
            }
        }

        // Northeast
        agent.findSensoryNeurons { idByte -> idByte in 0x20..0x3F }.forEach { neuron ->
            // Sensory neuron for detecting walls to the northeast
            if (isAgentOutOfBounds(agent)) {
                neuron.stimulateFromEnvironment(1.0)
            } else {
                neuron.stimulateFromEnvironment(getWallDistanceInDirection(agent, 1, -1))
            }
        }

        // East
        agent.findSensoryNeurons { idByte -> idByte in 0x40..0x5F }.forEach { neuron ->
            // Sensory neuron for detecting walls to the east
            if (isAgentOutOfBounds(agent)) {
                neuron.stimulateFromEnvironment(1.0)
            } else {
                neuron.stimulateFromEnvironment(getWallDistanceInDirection(agent, 1, 0))
            }
        }

        // Southeast
        agent.findSensoryNeurons { idByte -> idByte in 0x60..0x7F }.forEach { neuron ->
            // Sensory neuron for detecting walls to the southeast
            if (isAgentOutOfBounds(agent)) {
                neuron.stimulateFromEnvironment(1.0)
            } else {
                neuron.stimulateFromEnvironment(getWallDistanceInDirection(agent, 1, 1))
            }
        }

        // South
        agent.findSensoryNeurons { idByte -> idByte in 0x80..0x9F }.forEach { neuron ->
            // Sensory neuron for detecting walls to the south
            if (isAgentOutOfBounds(agent)) {
                neuron.stimulateFromEnvironment(1.0)
            } else {
                neuron.stimulateFromEnvironment(getWallDistanceInDirection(agent, 0, 1))
            }
        }

        // Southwest
        agent.findSensoryNeurons { idByte -> idByte in 0xA0..0xBF }.forEach { neuron ->
            // Sensory neuron for detecting walls to the southwest
            if (isAgentOutOfBounds(agent)) {
                neuron.stimulateFromEnvironment(1.0)
            } else {
                neuron.stimulateFromEnvironment(getWallDistanceInDirection(agent, -1, 1))
            }
        }

        // West
        agent.findSensoryNeurons { idByte -> idByte in 0xC0..0xDF }.forEach { neuron ->
            // Sensory neuron for detecting walls to the west
            if (isAgentOutOfBounds(agent)) {
                neuron.stimulateFromEnvironment(1.0)
            } else {
                neuron.stimulateFromEnvironment(getWallDistanceInDirection(agent, -1, 0))
            }
        }

        // Northwest
        agent.findSensoryNeurons { idByte -> idByte in 0xE0..0xFF }.forEach { neuron ->
            // Sensory neuron for detecting walls to the northwest
            if (isAgentOutOfBounds(agent)) {
                neuron.stimulateFromEnvironment(1.0)
            } else {
                neuron.stimulateFromEnvironment(getWallDistanceInDirection(agent, -1, -1))
            }
        }
    }

    private fun painReceptorSetup(agent: NeuralAgent) {
        agent.findPainReceptorNeurons().forEach { neuron ->
            agent.addNeuronAction(neuron) {
                if (isAgentOnWall(agent)) {
                    painAmount += 1.0
                    neuron.stimulateFromEnvironment(1.0)
                }
            }
        }
    }

    /**
     * Calculates the distance to the nearest wall in a given direction
     * @param agent The neural agent whose position will be used as the reference point
     * @param dx The change in x-coordinate per step (-1 for west, 0 for no change, 1 for east)
     * @param dy The change in y-coordinate per step (-1 for north, 0 for no change, 1 for south)
     * @return A value between 0.0 and 1.0, where 1.0 means very close to a wall and 0.0 means far from walls
     */
    private fun getWallDistanceInDirection(agent: NeuralAgent, dx: Int, dy: Int): Double {
        val currentPos = getAgentPosition(agent) ?: return 0.0
        var distance = 0
        var x = currentPos.x
        var y = currentPos.y
        
        // Maximum distance to check (half the world size)
        val maxDistance = minOf(world.width, world.height) / 2

        while (distance < maxDistance) {
            x += dx
            y += dy
            distance++

            if (world.isWall(x, y)) {
                // Return a value between 0.0 and 1.0, where closer walls give higher values
                // Using inverse square law to make the response more sensitive to nearby walls
                return 1.0 / (1.0 + (distance * distance))
            }
        }

        return 0.0
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

    fun isAgentOutOfBounds(agent: NeuralAgent): Boolean {
        val currentPos = getAgentPosition(agent) ?: return false
        return world.isOutOfBounds(currentPos.x, currentPos.y)
    }

}