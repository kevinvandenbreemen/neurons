package com.vandenbreemen.neurons.model

/**
 * A neuron that does nothing - it never fires and ignores all stimulation.
 * This can be used to create "dead zones" in the neural network.
 */
class DeadNeuron(weightCalculator: ConnectionWeightCalculator = StrengthBasedConnector) : Neuron(weightCalculator) {

    override fun stimulate(input: Double) {
        // Do nothing - ignore all stimulation
    }

    override fun fire() {
        // Do nothing - never fire
    }

    override fun applyStimulation() {
        // Do nothing - never update value
    }

    override fun updateAllConnectionWeights(learningRate: Double) {
        // Do nothing - never update weights
    }
} 