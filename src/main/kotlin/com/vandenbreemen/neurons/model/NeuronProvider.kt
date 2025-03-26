package com.vandenbreemen.neurons.model

interface NeuronProvider {

    fun getNeuron(): Neuron
}

class DefaultNeuronProvider(private val weightCalculator: ConnectionWeightCalculator = DefaultConnectionWeightCalculator()) :
    NeuronProvider {

    override fun getNeuron(): Neuron {
        return Neuron(weightCalculator)
    }

}

class RandomNeuronProvider(
    private val ratioSineToNeuron: Double,
    private val weightCalculator: ConnectionWeightCalculator = DefaultConnectionWeightCalculator()
) : NeuronProvider {

    override fun getNeuron(): Neuron {
        return if (Math.random() < ratioSineToNeuron) SineNeuron(weightCalculator) else Neuron(weightCalculator)
    }

}