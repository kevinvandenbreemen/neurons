package com.vandenbreemen.neurons.model

import kotlin.math.sin

class SineNeuron(
    private val timeIncrement: Float = 0.01f,
    weightCalculator: ConnectionWeightCalculator = DefaultConnectionWeightCalculator
) :
    Neuron(weightCalculator) {
    private var time = 0.0

    override val activation: Double
        get() = (sin(time) / 2.0) + 0.5

    override fun applyStimulation() {
        time += timeIncrement
    }

    override fun fire() {
        connections.forEach { it.neuron.stimulate(it.weight * activation) }
    }

    override fun stimulate(input: Double) {
        // Do nothing - this neuron doesn't respond to stimulation
    }

    override fun toString(): String {
        return "SineNeuron(value=$activation, connections=$connections)"
    }
}