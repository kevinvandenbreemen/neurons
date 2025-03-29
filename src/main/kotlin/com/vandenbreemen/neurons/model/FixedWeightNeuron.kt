package com.vandenbreemen.neurons.model

class FixedWeightNeuron(weightCalculator: ConnectionWeightCalculator = StrengthBasedConnector) :
    Neuron(weightCalculator) {

    override fun updateAllConnectionWeights(learningRate: Double) {
        // Do nothing - weights remain fixed
    }
} 