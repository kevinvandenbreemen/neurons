package com.vandenbreemen.neurons.model

interface NeuronProvider {

    fun getNeuron(): Neuron
}

class DefaultNeuronProvider : NeuronProvider {

    override fun getNeuron(): Neuron {
        return Neuron()
    }

}

class RandomNeuronProvider(private val ratioSineToNeuron: Double) : NeuronProvider {

    override fun getNeuron(): Neuron {
        return if (Math.random() < ratioSineToNeuron) SineNeuron() else Neuron()
    }

}