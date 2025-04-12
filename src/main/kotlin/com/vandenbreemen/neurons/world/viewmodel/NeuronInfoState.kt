package com.vandenbreemen.neurons.world.viewmodel

import com.vandenbreemen.neurons.model.Neuron

/**
 * Info about what a given neuron is doing
 */
class NeuronInfoState(private val neuron: Neuron) {
    val type: String get() = neuron::class.simpleName ?: "Unknown"
    val activation: Double get() = neuron.activation
    val connections: List<ConnectionInfo> get() = neuron.connections.map { ConnectionInfo(it.neuron, it.weight) }
    val sigmoidNumeratorMultiplier get() = neuron.sigmoidMultiplier
    val learningRate: Double get() = neuron.learningRateOverride
    val weightCalculatorTypeName: String
        get() = neuron.weightCalculator::class.simpleName ?: "Unknown"


    fun copy(): NeuronInfoState {
        return NeuronInfoState(neuron)
    }
}

data class ConnectionInfo(
    val targetNeuron: Neuron,
    val weight: Double
)