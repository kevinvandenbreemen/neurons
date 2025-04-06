package com.vandenbreemen.neurons.agent

import com.vandenbreemen.neurons.model.NeuralNet

/**
 * An agent that manages a neural network and provides methods for iteration and learning
 * @param neuralNet The neural network to be managed by this agent
 * @param learningRate The rate at which the network learns and updates its weights
 */
class NeuralAgent(
    private val neuralNet: NeuralNet,
    private val learningRate: Double
) {

    /**
     * Performs one iteration of the neural network, including firing, updating, and weight adjustments
     */
    fun iterate() {
        neuralNet.fireAndUpdate()
        neuralNet.updateAllWeights(learningRate)
    }
} 