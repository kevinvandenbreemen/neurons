package com.vandenbreemen.neurons.model

/**
 * A specialized neuron that provides a fixed 8-bit byte value representing a sensor ID.
 * This ID can be used by developers to determine what kind of sensory data this neuron will process.
 * Sensory neurons can only receive input from the environment and not from other neurons.
 */
class SensoryNeuron(
    val sensorId: Byte,
    weightCalculator: ConnectionWeightCalculator = StrengthBasedConnector
) : Neuron(weightCalculator) {

    override fun stimulate(input: Double) {
        // Do nothing - sensory neurons can only be stimulated by the environment
    }

    /**
     * Directly stimulate this sensory neuron with environmental data.
     * This is the only way to provide input to a sensory neuron.
     * @param input The environmental input value
     */
    fun stimulateFromEnvironment(input: Double) {
        super.stimulate(input)
    }

    override fun toString(): String {
        return "SensoryNeuron(sensorId=$sensorId, value=$activation, connections=$connections)"
    }
} 