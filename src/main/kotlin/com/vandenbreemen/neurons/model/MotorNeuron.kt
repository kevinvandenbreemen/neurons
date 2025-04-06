package com.vandenbreemen.neurons.model

/**
 * A specialized neuron that provides a fixed 8-bit byte value representing an action ID.
 * This ID can be used by developers to look up and execute specific actions.
 */
class MotorNeuron(
    val actionId: Byte,
    weightCalculator: ConnectionWeightCalculator = StrengthBasedConnector
) : Neuron(weightCalculator) {

    override fun toString(): String {
        return "MotorNeuron(actionId=$actionId, value=$activation, connections=$connections)"
    }
} 