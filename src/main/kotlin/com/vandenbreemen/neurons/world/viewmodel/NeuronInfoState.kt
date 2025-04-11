package com.vandenbreemen.neurons.world.viewmodel

import com.vandenbreemen.neurons.model.Neuron
import com.vandenbreemen.neurons.model.ThresholdNeuron

/**
 * Info about what a given neuron is doing
 */
class NeuronInfoState(private val neuron: Neuron) {
    val type: String get() = neuron::class.simpleName ?: "Unknown"
    val activation: Double get() = neuron.activation
    val connections: List<ConnectionInfo> get() = neuron.connections.map { ConnectionInfo(it.neuron, it.weight) }
    val threshold: Double? get() = if (neuron is ThresholdNeuron) neuron.threshold else null

    fun copy(): NeuronInfoState {
        return NeuronInfoState(neuron)
    }
}

data class ConnectionInfo(
    val targetNeuron: Neuron,
    val weight: Double
)