package com.vandenbreemen.neurons.model

import com.vandenbreemen.neurons.math.combinedSigmoidV1

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

    /**
     * Calculate the initial weight for a new connection between two neurons
     * @param sourceNeuron The source neuron
     * @param targetNeuron The target neuron
     * @return The initial weight for the connection
     */
    fun calculateStartingConnectionWeight(
        sourceNeuron: Neuron,
        targetNeuron: Neuron
    ): Double
}

private fun defaultSigmoidCalculation(sourceNeuron: Neuron, targetNeuron: Neuron): Double {
    return combinedSigmoidV1(
        sourceNeuron.activation,
        targetNeuron.activation,
        sourceNeuron.sigmoidMultiplier + targetNeuron.sigmoidMultiplier,
        1.0 / (sourceNeuron.sigmoidMultiplier + targetNeuron.sigmoidMultiplier),
        1.0,
        0.0
    )
}

object DefaultConnectionWeightCalculator : ConnectionWeightCalculator {
    override fun calculateWeight(
        sourceNeuron: Neuron,
        targetNeuron: Neuron,
        currentStrength: Double,
        learningRate: Double
    ): Double {
        return defaultSigmoidCalculation(
            sourceNeuron,
            targetNeuron
        )
    }

    override fun calculateStartingConnectionWeight(
        sourceNeuron: Neuron,
        targetNeuron: Neuron
    ): Double {
        // For default calculator, start with a random weight between -0.5 and 0.5
        return 0.0
    }
}

object StrengthBasedConnector : ConnectionWeightCalculator {
    override fun calculateWeight(
        sourceNeuron: Neuron,
        targetNeuron: Neuron,
        currentStrength: Double,
        learningRate: Double
    ): Double {
        //return currentStrength + learningRate * max(sourceNeuron.activation, targetNeuron.activation)
        return defaultSigmoidCalculation(
            sourceNeuron,
            targetNeuron
        )
    }

    override fun calculateStartingConnectionWeight(
        sourceNeuron: Neuron,
        targetNeuron: Neuron
    ): Double {
        // For strength-based connector, start with a random weight between 0 and 0.5
        return 0.0
    }
}