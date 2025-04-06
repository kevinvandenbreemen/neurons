package com.vandenbreemen.neurons.model

/**
 * A specialized neuron that provides a fixed 8-bit byte value representing a sensor ID.
 * This ID can be used by developers to determine what kind of sensory data this neuron will process.
 */
class SensoryNeuron(
    val sensorId: Byte,
    weightCalculator: ConnectionWeightCalculator = StrengthBasedConnector
) : Neuron(weightCalculator) {

    override fun toString(): String {
        return "SensoryNeuron(sensorId=$sensorId, value=$activation, connections=$connections)"
    }
} 