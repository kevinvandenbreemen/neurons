package com.vandenbreemen.neurons.model

import kotlin.math.exp

class Neuron {
    private val connections = mutableListOf<Connection>()

    private var value = 0.0
    val activation: Double
        get() = value

    fun connect(neuron: Neuron, strength: Double = 1.0) {
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