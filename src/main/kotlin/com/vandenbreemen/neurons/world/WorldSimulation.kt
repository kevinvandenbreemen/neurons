package com.vandenbreemen.neurons.world

import com.vandenbreemen.neurons.agent.NeuralAgent

/**
 * A simulation that manages a world and its neural agents
 * @param world The world for the simulation
 * @param initialAgents The initial set of neural agents in the simulation
 */
class WorldSimulation(
    val world: World = World(),
    initialAgents: List<NeuralAgent> = emptyList()
) {
    private val agents = mutableListOf<NeuralAgent>()
    private val agentPositions = mutableMapOf<NeuralAgent, AgentPosition>()

    init {
        agents.addAll(initialAgents)
        // Initialize positions for initial agents
        initialAgents.forEach { agent ->
            agentPositions[agent] = AgentPosition(0, 0) // Default starting position
        }
    }

    /**
     * Updates the simulation by one step
     * This causes all agents to perform one iteration of their neural networks
     */
    fun step() {
        agents.forEach { it.iterate() }
    }

    /**
     * Adds a new agent to the simulation
     * @param agent The agent to add
     * @param position The initial position of the agent (defaults to 0,0)
     */
    fun addAgent(agent: NeuralAgent, position: AgentPosition = AgentPosition(0, 0)) {
        agents.add(agent)
        agentPositions[agent] = position
    }

    /**
     * Removes an agent from the simulation
     * @param agent The agent to remove
     * @return true if the agent was found and removed, false otherwise
     */
    fun removeAgent(agent: NeuralAgent): Boolean {
        agentPositions.remove(agent)
        return agents.remove(agent)
    }

    /**
     * Gets all agents in the simulation
     */
    fun getAgents(): List<NeuralAgent> = agents.toList()

    /**
     * Gets the number of agents in the simulation
     */
    fun getAgentCount(): Int = agents.size

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
        if (agent in agents) {
            agentPositions[agent] = position
            return true
        }
        return false
    }
} 