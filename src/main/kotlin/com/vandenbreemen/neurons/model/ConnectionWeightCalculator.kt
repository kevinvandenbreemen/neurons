package com.vandenbreemen.neurons.model

import kotlin.math.abs

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

fun normalizedStrengthUpdate(
    sourceActivation: Double,
    targetActivation: Double,
    sourceMaxActivation: Double,
    targetMaxActivation: Double,
    currentStrength: Double,
    learningRate: Double
): Double {
    val maxPossibleSrc = (sourceMaxActivation)
    val maxPossibleTgt = (targetMaxActivation)
    val proxSrc = (sourceActivation) / maxPossibleSrc
    val proxTgt = (targetActivation) / maxPossibleTgt
    val difference = abs(proxSrc - proxTgt)
    val similarityFactor = 1.0 / (1.0 + difference) // Approaches 1 as difference approaches 0
    val delta = similarityFactor - currentStrength
    return currentStrength + (learningRate * delta)
}

object StrengthBasedConnector : ConnectionWeightCalculator {
    override fun calculateWeight(
        sourceNeuron: Neuron,
        targetNeuron: Neuron,
        currentStrength: Double,
        learningRate: Double
    ): Double {
        return normalizedStrengthUpdate(
            sourceNeuron.activation,
            targetNeuron.activation,
            sourceNeuron.maxActivationValue,
            targetNeuron.maxActivationValue,
            currentStrength,
            learningRate
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