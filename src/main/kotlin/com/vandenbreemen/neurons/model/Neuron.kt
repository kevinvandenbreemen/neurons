package com.vandenbreemen.neurons.model

import kotlin.math.exp

class Neuron {
    internal val connections = mutableListOf<Connection>()

    private var value = 0.0
    val activation: Double
        get() = value

    fun connect(neuron: Neuron, strength: Double = 1.0) {
        if (strength > 1.0f) throw IllegalArgumentException("Strength must be between 0 and 1")
        //  Don't allow more than one connection to same neuron
        if (connections.any { it.neuron == neuron }) return
        connections.add(Connection(neuron, strength))
    }

    fun stimulate(input: Double) {
        value = sigmoid(value + input)
    }

    fun sigmoid(x: Double): Double {
        return 1 / (1 + exp(-x))
    }

    fun fire() {
        connections.forEach { it.neuron.stimulate(it.strength * value) }
    }
}

data class Connection(val neuron: Neuron, val strength: Double)