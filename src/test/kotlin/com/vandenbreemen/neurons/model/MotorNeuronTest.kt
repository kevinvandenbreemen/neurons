package com.vandenbreemen.neurons.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MotorNeuronTest {

    @Test
    fun `should maintain assigned action ID`() {
        // Given
        val actionId: Byte = 0x42
        val neuron = MotorNeuron(actionId)

        // Then
        assertEquals(actionId, neuron.actionId, "Action ID should match assigned value")
    }

    @Test
    fun `should maintain normal neuron behavior`() {
        // Given
        val motorNeuron = MotorNeuron(0x01)
        val targetNeuron = Neuron()

        // When
        motorNeuron.connect(targetNeuron, 0.5)
        motorNeuron.stimulate(1.0)
        motorNeuron.applyStimulation()
        motorNeuron.fire()
        targetNeuron.applyStimulation()

        // Then
        assert(targetNeuron.activation > 0.0) { "Target neuron should receive stimulation" }
    }
} 