package com.vandenbreemen.neurons.model

class Neuron {
    private val connections = mutableListOf<Connection>()

    fun connect(neuron: Neuron, strength: Double = 1.0) {
        connections.add(Connection(neuron, strength))
    }
}

data class Connection(val neuron: Neuron, val strength: Double)