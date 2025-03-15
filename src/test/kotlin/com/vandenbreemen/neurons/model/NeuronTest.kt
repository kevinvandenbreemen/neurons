package com.vandenbreemen.neurons.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NeuronTest {

    @Test
    fun `should connect neurons and verify connection strength`() {
        // Given
        val sourceNeuron = Neuron()
        val targetNeuron = Neuron()
        val initialStrength = 0.5

        // When
        sourceNeuron.connect(targetNeuron, initialStrength)

        // Then
        val connection = sourceNeuron.connections.find { it.neuron == targetNeuron }
        assertTrue(connection != null, "Connection should exist")
        assertEquals(initialStrength, connection!!.strength, "Connection strength should match initial value")
    }

    @Test
    fun `should fire and propagate activation to connected neuron`() {
        // Given
        val sourceNeuron = Neuron()
        val targetNeuron = Neuron()
        val connectionStrength = 0.5
        sourceNeuron.connect(targetNeuron, connectionStrength)

        // When
        sourceNeuron.stimulate(1.0)  // Stimulate source neuron
        sourceNeuron.applyStimulation()  // Apply the stimulation
        sourceNeuron.fire()  // Fire the neuron
        targetNeuron.applyStimulation()  // Apply the received stimulation to target

        // Then
        assertTrue(targetNeuron.activation > 0.0, "Target neuron should have received activation")
        assertTrue(
            targetNeuron.activation <= connectionStrength,
            "Target neuron activation should not exceed connection strength"
        )
    }

    @Test
    fun `should not allow connection strength greater than 1`() {
        // Given
        val sourceNeuron = Neuron()
        val targetNeuron = Neuron()

        // When/Then
        try {
            sourceNeuron.connect(targetNeuron, 1.5)
            throw AssertionError("Should have thrown IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            // Expected
        }
    }

    @Test
    fun `should not create duplicate connections to same neuron`() {
        // Given
        val sourceNeuron = Neuron()
        val targetNeuron = Neuron()

        // When
        sourceNeuron.connect(targetNeuron, 0.5)
        sourceNeuron.connect(targetNeuron, 0.7)  // Try to connect again with different strength

        // Then
        assertEquals(1, sourceNeuron.connections.size, "Should only have one connection")
        assertEquals(0.5, sourceNeuron.connections[0].strength, "Should maintain original connection strength")
    }
} 