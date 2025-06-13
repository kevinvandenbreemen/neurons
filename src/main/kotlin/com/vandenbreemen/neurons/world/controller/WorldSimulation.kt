package com.vandenbreemen.neurons.world.controller

import com.vandenbreemen.neurons.agent.NeuralAgent
import com.vandenbreemen.neurons.world.model.World

/**
 * A simulation that manages a world and its neural agents
 * @param world The world for the simulation
 * @param initialAgents The initial set of neural agents in the simulation
 */
open class WorldSimulation(
    val world: World = World(),
    initialAgents: List<NeuralAgent> = emptyList()
) {
    private val agents = mutableListOf<NeuralAgent>()

    init {
        initialAgents.forEach { agent ->
            addAgent(agent)
        }
    }

    /**
     * Allows you to configure what the agent will do etc.  Note that setup code may NOT know
     * anything about the agent's position in the world or any other detail about the world setup.  This is
     * by design
     */
    open fun doAgentSetup(agent: NeuralAgent) {

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
     */
    fun addAgent(agent: NeuralAgent) {
        agents.add(agent)
        doAgentSetup(agent)
    }

    /**
     * Removes an agent from the simulation
     * @param agent The agent to remove
     * @return true if the agent was found and removed, false otherwise
     */
    open fun removeAgent(agent: NeuralAgent): Boolean {
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
} 