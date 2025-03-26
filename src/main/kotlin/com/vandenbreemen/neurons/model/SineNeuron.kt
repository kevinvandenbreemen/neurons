package com.vandenbreemen.neurons.model

import kotlin.math.sin

class SineNeuron : Neuron() {
    private var time = 0.0

    override val activation: Double
        get() = sin(time)

    override fun applyStimulation() {
        time += 0.1f
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