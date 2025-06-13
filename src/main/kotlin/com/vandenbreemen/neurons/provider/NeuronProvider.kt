package com.vandenbreemen.neurons.provider

import com.vandenbreemen.neurons.model.ConnectionWeightCalculator
import com.vandenbreemen.neurons.model.DefaultConnectionWeightCalculator
import com.vandenbreemen.neurons.model.FixedWeightNeuron
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

class RandomNeuronProvider(
    private val fixedWeightNeuronPercentage: Double = 0.0,
    private val weightCalculator: ConnectionWeightCalculator = DefaultConnectionWeightCalculator
) : NeuronProvider {

    init {
        require(fixedWeightNeuronPercentage <= 1.0) { "Percentage must be 1.0 or less, but is $fixedWeightNeuronPercentage" }
        require(fixedWeightNeuronPercentage >= 0) {
            "Percentage must be between 0 and 1"
        }
    }

    private val regularNeuronPercentage: Double
        get() = 1.0 - fixedWeightNeuronPercentage

    override fun getNeuron(): Neuron {
        val random = Math.random()
        return when {
            random < regularNeuronPercentage -> Neuron(weightCalculator)
            else -> FixedWeightNeuron(weightCalculator)
        }
    }

}