package com.vandenbreemen.neurons.model

/**
 * A specialized neuron that can only be stimulated by external sources (like pain).
 * When fired, it sends a signal value of 1 to all adjacent neurons.
 */
class PainReceptorNeuron(
    weightCalculator: ConnectionWeightCalculator = StrengthBasedConnector
) : Neuron(weightCalculator) {

    override fun stimulate(input: Double) {
        // Do nothing - pain receptors can only be stimulated by the environment
    }

    /**
     * Directly stimulate this pain receptor with environmental data.
     * This is the only way to provide input to a pain receptor.
     * @param input The environmental input value
     */
    fun stimulateFromEnvironment(input: Double) {
        super.stimulate(input)
    }

    override fun fire() {
        // Always send a value of 1 to all connected neurons
        connections.forEach { it.neuron.stimulate(it.weight * 1.0) }
    }

    override fun toString(): String {
        return "PainReceptorNeuron(value=$activation, connections=$connections)"
    }
} 