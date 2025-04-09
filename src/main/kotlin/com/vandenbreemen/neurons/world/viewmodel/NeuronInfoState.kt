package com.vandenbreemen.neurons.world.viewmodel

import com.vandenbreemen.neurons.model.Neuron

/**
 * Info about what a given neuron is doing
 */
class NeuronInfoState(private val neuron: Neuron) {
    val type: String get() = neuron::class.simpleName ?: "Unknown"
    val activation: Double get() = neuron.activation
    fun copy(): NeuronInfoState {
        return NeuronInfoState(neuron)
    }
}