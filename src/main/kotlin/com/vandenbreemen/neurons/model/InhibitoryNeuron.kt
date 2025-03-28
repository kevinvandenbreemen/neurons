package com.vandenbreemen.neurons.model

class InhibitoryNeuron(weightCalculator: ConnectionWeightCalculator = StrengthBasedConnector) :
    Neuron(weightCalculator) {

    override fun fire() {
        // Calculate inverse of current stimulation level
        val inverseStimulation = 0 - activation

        // Fire with inverse stimulation strength
        connections.forEach { it.neuron.stimulate(it.weight * inverseStimulation) }
    }
} 