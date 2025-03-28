package com.vandenbreemen.neurons.model

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
    private val inhibitoryNeuronPercentage: Double,
    private val sineNeuronPercentage: Double,
    private val weightCalculator: ConnectionWeightCalculator = DefaultConnectionWeightCalculator
) : NeuronProvider {

    init {
        val total = inhibitoryNeuronPercentage + sineNeuronPercentage
        require(total <= 1.0) { "Percentages must sum to 1.0 or less, but sum to $total" }
        require(inhibitoryNeuronPercentage >= 0 && sineNeuronPercentage >= 0) {
            "All percentages must be between 0 and 1"
        }
    }

    private val regularNeuronPercentage: Double
        get() = 1.0 - inhibitoryNeuronPercentage - sineNeuronPercentage

    override fun getNeuron(): Neuron {
        val random = Math.random()
        return when {
            random < regularNeuronPercentage -> Neuron(weightCalculator)
            random < regularNeuronPercentage + inhibitoryNeuronPercentage -> InhibitoryNeuron(weightCalculator)
            else -> SineNeuron(0.01f, weightCalculator)
        }
    }

}