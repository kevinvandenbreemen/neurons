package com.vandenbreemen.neurons.model

/**
 * A neuron that only fires when its stimulation reaches a certain threshold.
 * The threshold is a value between 0 and 1, and the neuron will only fire
 * when the stimulation reaches or exceeds this threshold.
 */
class ThresholdNeuron(
    val threshold: Double,
    weightCalculator: ConnectionWeightCalculator = StrengthBasedConnector
) : Neuron(weightCalculator) {

    init {
        require(threshold in 0.0..1.0) { "Threshold must be between 0 and 1, value passed was $threshold" }
    }

    override fun applyStimulation() {
        value = if (stimulationValue >= threshold) 1.0 else 0.0
        stimulationValue = 0.0
    }

    override fun toString(): String {
        return "ThresholdNeuron(threshold=$threshold, value=$activation, connections=$connections)"
    }
} 