package com.vandenbreemen.neurons.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SensoryNeuronTest {

    @Test
    fun `should maintain assigned sensor ID`() {
        // Given
        val sensorId: Byte = 0x42
        val neuron = SensoryNeuron(sensorId)

        // Then
        assertEquals(sensorId, neuron.sensorId, "Sensor ID should match assigned value")
    }

    @Test
    fun `should maintain normal neuron behavior`() {
        // Given
        val sensoryNeuron = SensoryNeuron(0x01)
        val targetNeuron = Neuron()

        // When
        sensoryNeuron.connect(targetNeuron, 0.5)
        sensoryNeuron.stimulate(1.0)
        sensoryNeuron.applyStimulation()
        sensoryNeuron.fire()
        targetNeuron.applyStimulation()

        // Then
        assertTrue(targetNeuron.activation > 0.0, "Target neuron should receive stimulation")
    }
} 