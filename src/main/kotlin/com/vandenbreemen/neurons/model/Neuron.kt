package com.vandenbreemen.neurons.model

import kotlin.math.absoluteValue
import kotlin.math.exp

class Neuron {
    internal val connections = mutableListOf<Connection>()

    /**
     * Current value of this neuron.
     */
    private var value = 0.0
    val activation: Double
        get() = value

    /**
     * Current amount of stimulation this neuron has received.  Note that this is not the same as the value of the neuron.
     */
    private var stimulationValue = 0.0

    fun connect(neuron: Neuron, strength: Double = 1.0) {
        if (strength.absoluteValue > 1.0f) throw IllegalArgumentException("Strength must be between -1 and 1")
        //  Don't allow more than one connection to same neuron
        if (connections.any { it.neuron == neuron }) return
        connections.add(Connection(neuron, strength))
    }

    fun stimulate(input: Double) {
        stimulationValue = sigmoid(stimulationValue + input)
    }

    /**
     * Update the value to be the total stimulation received
     */
    fun applyStimulation() {
        value = stimulationValue
        stimulationValue = 0.0
    }

    fun sigmoid(x: Double): Double {
        return 1 / (1 + exp(-x))
    }

    /**
     * Updates all connection weights based on Hebbian learning.
     * The weight change is determined by the correlation between this neuron's activation
     * and each target neuron's activation.
     * @param learningRate How quickly the weights should change (default 0.1)
     */
    fun updateAllConnectionWeights(learningRate: Double = 0.1) {
        val updatedConnections = connections.map { connection ->
            // Calculate weight update based on correlation of activations
            val correlation = value * connection.neuron.activation

            // Map correlation through sigmoid and scale to [-1,1] range
            val weightDelta = (sigmoid(correlation) * 2) - 1

            // Update weight ensuring it stays in [-1,1] range
            val newStrength = (connection.strength + (weightDelta * learningRate)).coerceIn(-1.0, 1.0)

            // Create new connection with updated weight
            Connection(connection.neuron, newStrength)
        }

        // Replace all connections with their updated versions
        connections.clear()
        connections.addAll(updatedConnections)
    }

    fun fire() {
        connections.forEach { it.neuron.stimulate(it.strength * value) }
    }

    override fun toString(): String {
        return "Neuron(value=$value, connections=$connections)"
    }
}

data class Connection(val neuron: Neuron, val strength: Double) {
    override fun toString(): String {
        return "Connection to neuron:  strength=$strength"
    }
}