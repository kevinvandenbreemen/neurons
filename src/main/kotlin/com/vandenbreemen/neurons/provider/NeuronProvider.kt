package com.vandenbreemen.neurons.provider

import com.vandenbreemen.neurons.model.ConnectionWeightCalculator
import com.vandenbreemen.neurons.model.DefaultConnectionWeightCalculator
import com.vandenbreemen.neurons.model.Neuron

interface NeuronProvider {

    fun getNeuron(): Neuron
}

class DefaultNeuronProvider(private val weightCalculator: ConnectionWeightCalculator = DefaultConnectionWeightCalculator) :
    NeuronProvider {

    override fun getNeuron(): Neuron {
        return Neuron(weightCalculator)
    }

}