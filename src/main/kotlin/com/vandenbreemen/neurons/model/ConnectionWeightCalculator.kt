package com.vandenbreemen.neurons.model

import kotlin.math.max
import kotlin.random.Random

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

object DefaultConnectionWeightCalculator : ConnectionWeightCalculator {
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

    override fun calculateStartingConnectionWeight(
        sourceNeuron: Neuron,
        targetNeuron: Neuron
    ): Double {
        // For default calculator, start with a random weight between -0.5 and 0.5
        return Random.nextDouble(-0.5, 0.5)
    }
}

object StrengthBasedConnector : ConnectionWeightCalculator {
    override fun calculateWeight(
        sourceNeuron: Neuron,
        targetNeuron: Neuron,
        currentStrength: Double,
        learningRate: Double
    ): Double {
        return currentStrength + learningRate * max(sourceNeuron.activation, targetNeuron.activation)
    }

    override fun calculateStartingConnectionWeight(
        sourceNeuron: Neuron,
        targetNeuron: Neuron
    ): Double {
        // For strength-based connector, start with a random weight between 0 and 0.5
        return Random.nextDouble(0.0, 0.5)
    }
}