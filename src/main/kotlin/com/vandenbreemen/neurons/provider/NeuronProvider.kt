package com.vandenbreemen.neurons.provider

import com.vandenbreemen.neurons.model.ConnectionWeightCalculator
import com.vandenbreemen.neurons.model.Neuron
import com.vandenbreemen.neurons.model.StrengthBasedConnector

interface NeuronProvider {

    fun getNeuron(): Neuron
}

class DefaultNeuronProvider(private val weightCalculator: ConnectionWeightCalculator = StrengthBasedConnector) :
    NeuronProvider {

    override fun getNeuron(): Neuron {
        return Neuron(weightCalculator)
    }

}