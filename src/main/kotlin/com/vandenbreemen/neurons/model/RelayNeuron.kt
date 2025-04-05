package com.vandenbreemen.neurons.model

/**
 * A neuron that relays its value to only one connected neuron while sending 0 to all others.
 * The target neuron is determined by the first connection made to this neuron.
 */
class RelayNeuron(weightCalculator: ConnectionWeightCalculator = StrengthBasedConnector) : Neuron(weightCalculator) {

    private var targetNeuron: Neuron? = null
    var direction: Direction? = null

    fun setTargetNeuron(neuron: Neuron) {
        if (connections.any { it.neuron == neuron }) {
            targetNeuron = neuron
        } else {
            throw IllegalArgumentException("Target neuron must be connected first")
        }
    }

    fun getTargetNeuron(): Neuron? = targetNeuron

    override fun fire() {
        connections.forEach { connection ->
            if (connection.neuron == targetNeuron) {
                connection.neuron.stimulate(connection.weight * activation)
            } else {
                connection.neuron.stimulate(0.0)
            }
        }
    }
} 