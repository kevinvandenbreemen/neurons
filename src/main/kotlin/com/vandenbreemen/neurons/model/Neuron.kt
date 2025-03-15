package com.vandenbreemen.neurons.model

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
        if (strength > 1.0f) throw IllegalArgumentException("Strength must be between 0 and 1")
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

    fun fire() {
        connections.forEach { it.neuron.stimulate(it.strength * value) }
    }
}

data class Connection(val neuron: Neuron, val strength: Double)