package com.vandenbreemen.neurons.world.controller

import com.vandenbreemen.neurons.agent.NeuralAgent
import com.vandenbreemen.neurons.model.MotorNeuronDirections
import com.vandenbreemen.neurons.world.model.AgentPosition
import com.vandenbreemen.neurons.world.model.World

data class MovementVector(val dx: Double, val dy: Double)

class NavigationWorldSimulation(
    world: World = World(),
    private val maxMovementDelta: Int = 1
) : WorldSimulation(world) {

    private val agentPositions = mutableMapOf<NeuralAgent, AgentPosition>()
    private val agentMovementVectors = mutableMapOf<NeuralAgent, MutableList<MovementVector>>()

    /**
     * Adds a new agent to the simulation
     * @param agent The agent to add
     * @param position The initial position of the agent
     */
    fun addAgent(agent: NeuralAgent, position: AgentPosition) {
        super.addAgent(agent)
        agentPositions[agent] = position
    }

    /**
     * Removes an agent from the simulation
     * @param agent The agent to remove
     * @return true if the agent was found and removed, false otherwise
     */
    override fun removeAgent(agent: NeuralAgent): Boolean {
        agentPositions.remove(agent)
        return super.removeAgent(agent)
    }

    /**
     * Gets the position of an agent
     * @param agent The agent to get the position for
     * @return The agent's position, or null if the agent is not in the simulation
     */
    fun getAgentPosition(agent: NeuralAgent): AgentPosition? = agentPositions[agent]

    /**
     * Sets the position of an agent
     * @param agent The agent to set the position for
     * @param position The new position
     * @return true if the agent exists and the position was set, false otherwise
     */
    fun setAgentPosition(agent: NeuralAgent, position: AgentPosition): Boolean {
        if (agent in getAgents()) {
            agentPositions[agent] = position
            return true
        }
        return false
    }

    override fun doAgentSetup(agent: NeuralAgent) {
        agentMovementVectors[agent] = mutableListOf()
        motorNeuronSetup(agent)
        sensoryNeuronSetup(agent)
        painReceptorSetup(agent)
    }

    private fun motorNeuronSetup(agent: NeuralAgent) {
        // North
        agent.findMotorNeurons { idByte -> idByte in MotorNeuronDirections.NORTH_START..MotorNeuronDirections.NORTH_END }
            .forEach { neuron ->
            agent.addNeuronAction(neuron) {
                val delta = neuron.activation * maxMovementDelta
                agentMovementVectors[agent]?.add(MovementVector(0.0, -delta))
            }
        }

        // Northeast
        agent.findMotorNeurons { idByte -> idByte in MotorNeuronDirections.NORTHEAST_START..MotorNeuronDirections.NORTHEAST_END }
            .forEach { neuron ->
            agent.addNeuronAction(neuron) {
                val delta = neuron.activation * maxMovementDelta
                agentMovementVectors[agent]?.add(MovementVector(delta, -delta))
            }
        }

        // East
        agent.findMotorNeurons { idByte -> idByte in MotorNeuronDirections.EAST_START..MotorNeuronDirections.EAST_END }
            .forEach { neuron ->
            agent.addNeuronAction(neuron) {
                val delta = neuron.activation * maxMovementDelta
                agentMovementVectors[agent]?.add(MovementVector(delta, 0.0))
            }
        }

        // Southeast
        agent.findMotorNeurons { idByte -> idByte in MotorNeuronDirections.SOUTHEAST_START..MotorNeuronDirections.SOUTHEAST_END }
            .forEach { neuron ->
            agent.addNeuronAction(neuron) {
                val delta = neuron.activation * maxMovementDelta
                agentMovementVectors[agent]?.add(MovementVector(delta, delta))
            }
        }

        // South
        agent.findMotorNeurons { idByte -> idByte in MotorNeuronDirections.SOUTH_START..MotorNeuronDirections.SOUTH_END }
            .forEach { neuron ->
            agent.addNeuronAction(neuron) {
                val delta = neuron.activation * maxMovementDelta
                agentMovementVectors[agent]?.add(MovementVector(0.0, delta))
            }
        }

        // Southwest
        agent.findMotorNeurons { idByte -> idByte in MotorNeuronDirections.SOUTHWEST_START..MotorNeuronDirections.SOUTHWEST_END }
            .forEach { neuron ->
            agent.addNeuronAction(neuron) {
                val delta = neuron.activation * maxMovementDelta
                agentMovementVectors[agent]?.add(MovementVector(-delta, delta))
            }
        }

        // West
        agent.findMotorNeurons { idByte -> idByte in MotorNeuronDirections.WEST_START..MotorNeuronDirections.WEST_END }
            .forEach { neuron ->
            agent.addNeuronAction(neuron) {
                val delta = neuron.activation * maxMovementDelta
                agentMovementVectors[agent]?.add(MovementVector(-delta, 0.0))
            }
        }

        // Northwest
        agent.findMotorNeurons { idByte -> idByte in MotorNeuronDirections.NORTHWEST_START..MotorNeuronDirections.NORTHWEST_END }
            .forEach { neuron ->
            agent.addNeuronAction(neuron) {
                val delta = neuron.activation * maxMovementDelta
                agentMovementVectors[agent]?.add(MovementVector(-delta, -delta))
            }
        }
    }

    private fun sensoryNeuronSetup(agent: NeuralAgent) {
        // North
        agent.findSensoryNeurons { idByte -> idByte in MotorNeuronDirections.NORTH_START..MotorNeuronDirections.NORTH_END }
            .forEach { neuron ->
            // Sensory neuron for detecting walls to the north
            neuron.stimulateFromEnvironment(getWallDistanceInDirection(agent, 0, -1))
        }

        // Northeast
        agent.findSensoryNeurons { idByte -> idByte in MotorNeuronDirections.NORTHEAST_START..MotorNeuronDirections.NORTHEAST_END }
            .forEach { neuron ->
            // Sensory neuron for detecting walls to the northeast
            neuron.stimulateFromEnvironment(getWallDistanceInDirection(agent, 1, -1))
        }

        // East
        agent.findSensoryNeurons { idByte -> idByte in MotorNeuronDirections.EAST_START..MotorNeuronDirections.EAST_END }
            .forEach { neuron ->
            // Sensory neuron for detecting walls to the east
            neuron.stimulateFromEnvironment(getWallDistanceInDirection(agent, 1, 0))
        }

        // Southeast
        agent.findSensoryNeurons { idByte -> idByte in MotorNeuronDirections.SOUTHEAST_START..MotorNeuronDirections.SOUTHEAST_END }
            .forEach { neuron ->
            // Sensory neuron for detecting walls to the southeast
            neuron.stimulateFromEnvironment(getWallDistanceInDirection(agent, 1, 1))
        }

        // South
        agent.findSensoryNeurons { idByte -> idByte in MotorNeuronDirections.SOUTH_START..MotorNeuronDirections.SOUTH_END }
            .forEach { neuron ->
            // Sensory neuron for detecting walls to the south
            neuron.stimulateFromEnvironment(getWallDistanceInDirection(agent, 0, 1))
        }

        // Southwest
        agent.findSensoryNeurons { idByte -> idByte in MotorNeuronDirections.SOUTHWEST_START..MotorNeuronDirections.SOUTHWEST_END }
            .forEach { neuron ->
            // Sensory neuron for detecting walls to the southwest
            neuron.stimulateFromEnvironment(getWallDistanceInDirection(agent, -1, 1))
        }

        // West
        agent.findSensoryNeurons { idByte -> idByte in MotorNeuronDirections.WEST_START..MotorNeuronDirections.WEST_END }
            .forEach { neuron ->
            // Sensory neuron for detecting walls to the west
            neuron.stimulateFromEnvironment(getWallDistanceInDirection(agent, -1, 0))
        }

        // Northwest
        agent.findSensoryNeurons { idByte -> idByte in MotorNeuronDirections.NORTHWEST_START..MotorNeuronDirections.NORTHWEST_END }
            .forEach { neuron ->
            // Sensory neuron for detecting walls to the northwest
            neuron.stimulateFromEnvironment(getWallDistanceInDirection(agent, -1, -1))
        }
    }

    private fun painReceptorSetup(agent: NeuralAgent) {
        agent.findPainReceptorNeurons().forEach { neuron ->
            agent.addNeuronAction(neuron) {
                if (isAgentCollidingWithSomething(agent)) {
                    neuron.stimulateFromEnvironment(1.0)
                }
            }
        }
    }

    /**
     * Steps through the simulation, allowing each agent to perform its actions
     * and checking for collisions after all agents have moved.
     */
    override fun step() {
        // Clear movement vectors before processing
        agentMovementVectors.values.forEach { it.clear() }
        
        super.step()

        // Apply accumulated movement vectors
        getAgents().forEach { agent ->
            val currentPos = getAgentPosition(agent) ?: return@forEach
            val vectors = agentMovementVectors[agent] ?: return@forEach

            if (vectors.isNotEmpty()) {
                // Sum up all vectors
                val totalDx = vectors.sumOf { it.dx }
                val totalDy = vectors.sumOf { it.dy }

                // Calculate new position with wrapping
                val newX = (currentPos.x + totalDx.toInt() + world.width) % world.width
                val newY = (currentPos.y + totalDy.toInt() + world.height) % world.height

                setAgentPosition(agent, currentPos.copy(x = newX, y = newY))
            }
        }

        // After all agents have iterated, check for collisions
        getAgents().forEach { agent ->
            if (isAgentCollidingWithSomething(agent)) {
                incrementErrorCount()
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
            x = (x + dx + world.width) % world.width // Wrap around x
            y = (y + dy + world.height) % world.height // Wrap around y
            distance++

            if (world.isBoundary(x, y)) {
                // Return a value between 0.0 and 1.0, where closer walls give higher values
                // Using inverse square law to make the response more sensitive to nearby walls
                return 1.0 / (1.0 + (distance * distance))
            }
        }

        return 0.0
    }

    /**
     * Checks if the agent is on a wall
     */
    private fun isAgentCollidingWithSomething(agent: NeuralAgent): Boolean {
        val currentPos = getAgentPosition(agent) ?: return false
        return world.isBoundary(currentPos.x, currentPos.y)
    }
}