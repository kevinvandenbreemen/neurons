package com.vandenbreemen.neurons.model

import kotlin.math.absoluteValue
import kotlin.math.exp

open class Neuron {
    internal val connections = mutableListOf<Connection>()

    /**
     * Current value of this neuron.
     */
    private var value = 0.0
    open val activation: Double
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

    open fun stimulate(input: Double) {
        stimulationValue = (stimulationValue + input)
    }

    /**
     * Update the value to be the total stimulation received
     */
    open fun applyStimulation() {
        value = sigmoid(stimulationValue)
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

            val myStrength = this.activation
            val theirStrength = connection.neuron.activation
            val rawDiff = myStrength - theirStrength
            val delta = rawDiff * learningRate
            val newStrength = (connection.strength + delta)


            Connection(connection.neuron, newStrength)
        }

        // Replace all connections with their updated versions
        connections.clear()
        connections.addAll(updatedConnections)
    }

    open fun fire() {
        connections.forEach { it.neuron.stimulate(it.weight * value) }
    }

    override fun toString(): String {
        return "Neuron(value=$value, connections=$connections)"
    }
}

data class Connection(
    val neuron: Neuron,
    val strength: Double,
    private val sigmoidCalculator: (Double) -> Double = { x -> 1 / (1 + exp(-x)) }
) {

    val weight: Double
        get() = strength

    override fun toString(): String {
        return "Connection to neuron:  strength=$strength"
    }
}