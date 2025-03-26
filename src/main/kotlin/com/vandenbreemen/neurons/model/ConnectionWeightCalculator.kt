package com.vandenbreemen.neurons.model

interface ConnectionWeightCalculator {
    /**
     * Calculate the weight for a connection between two neurons
     * @param sourceNeuron The source neuron
     * @param targetNeuron The target neuron
     * @param currentStrength The current strength of the connection
     * @param learningRate How quickly the weights should change
     * @return The new weight for the connection
     */
    fun calculateWeight(
        sourceNeuron: Neuron,
        targetNeuron: Neuron,
        currentStrength: Double,
        learningRate: Double
    ): Double
}

class DefaultConnectionWeightCalculator : ConnectionWeightCalculator {
    override fun calculateWeight(
        sourceNeuron: Neuron,
        targetNeuron: Neuron,
        currentStrength: Double,
        learningRate: Double
    ): Double {
        val myStrength = sourceNeuron.activation
        val theirStrength = targetNeuron.activation
        val rawDiff = myStrength - theirStrength
        val delta = rawDiff
        return (currentStrength + delta)
    }
} 