package com.vandenbreemen.neurons.model

import org.junit.jupiter.api.Test
import kotlin.math.absoluteValue
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NeuronTest {

    @Test
    fun `should connect neurons and verify connection strength`() {
        // Given
        val sourceNeuron = Neuron()
        val targetNeuron = Neuron()
        val initialStrength = -0.5

        // When
        sourceNeuron.connect(targetNeuron, initialStrength)

        // Then
        val connection = sourceNeuron.connections.find { it.neuron == targetNeuron }
        assertTrue(connection != null, "Connection should exist")
        assertEquals(initialStrength, connection!!.strength, "Connection strength should match initial value")
    }

    @Test
    fun `should fire and propagate activation with negative connection strength`() {
        // Given
        val sourceNeuron = Neuron()
        val targetNeuron = Neuron()
        val connectionStrength = -0.5
        sourceNeuron.connect(targetNeuron, connectionStrength)

        // When
        sourceNeuron.stimulate(1.0)  // Stimulate source neuron
        sourceNeuron.applyStimulation()  // Apply the stimulation
        sourceNeuron.fire()  // Fire the neuron
        targetNeuron.applyStimulation()  // Apply the received stimulation to target

        // Then
        assertEquals(
            targetNeuron.sigmoid(
                sourceNeuron.sigmoid(1.0) * connectionStrength
            ), targetNeuron.activation
        )
    }

    @Test
    fun `should not allow connection strength outside -1 to 1 range`() {
        // Given
        val sourceNeuron = Neuron()
        val targetNeuron = Neuron()

        // When/Then - Test upper bound
        try {
            sourceNeuron.connect(targetNeuron, 1.5)
            throw AssertionError("Should have thrown IllegalArgumentException for strength > 1")
        } catch (e: IllegalArgumentException) {
            // Expected
        }

        // When/Then - Test lower bound
        try {
            sourceNeuron.connect(targetNeuron, -1.5)
            throw AssertionError("Should have thrown IllegalArgumentException for strength < -1")
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
        sourceNeuron.connect(targetNeuron, -0.5)
        sourceNeuron.connect(targetNeuron, 0.7)  // Try to connect again with different strength

        // Then
        assertEquals(1, sourceNeuron.connections.size, "Should only have one connection")
        assertEquals(-0.5, sourceNeuron.connections[0].strength, "Should maintain original connection strength")
    }

    @Test
    fun `should handle both positive and negative connection strengths`() {
        // Given
        val sourceNeuron = Neuron()
        val targetNeuron1 = Neuron()
        val targetNeuron2 = Neuron()

        // When
        sourceNeuron.connect(targetNeuron1, 0.5)  // Excitatory connection
        sourceNeuron.connect(targetNeuron2, -0.5) // Inhibitory connection
        sourceNeuron.stimulate(1.0)
        sourceNeuron.applyStimulation()
        sourceNeuron.fire()
        targetNeuron1.applyStimulation()
        targetNeuron2.applyStimulation()

        // Then
        assertTrue(targetNeuron1.activation > 0.5, "Excitatory connection should increase activation")
        assertTrue(targetNeuron2.activation < 0.5, "Inhibitory connection should decrease activation")
    }

    @Test
    fun `should update all connection weights based on correlated activity`() {
        // Given
        val sourceNeuron = Neuron()
        val targetNeuron1 = Neuron()
        val targetNeuron2 = Neuron()

        // Create connections with initial weights
        sourceNeuron.connect(targetNeuron1, 0.0)  // Neutral connection
        sourceNeuron.connect(targetNeuron2, 0.0)  // Neutral connection

        // When - Set up correlated activity with first target, anti-correlated with second
        sourceNeuron.stimulate(1.0)
        targetNeuron1.stimulate(1.0)  // Correlated activity
        targetNeuron2.stimulate(-1.0) // Anti-correlated activity

        sourceNeuron.applyStimulation()
        targetNeuron1.applyStimulation()
        targetNeuron2.applyStimulation()

        // Update weights with high learning rate for visible effect
        sourceNeuron.updateAllConnectionWeights(learningRate = 0.5)

        // Then
        val conn1 = sourceNeuron.connections.find { it.neuron == targetNeuron1 }!!
        val conn2 = sourceNeuron.connections.find { it.neuron == targetNeuron2 }!!

        println(sourceNeuron)
        println(targetNeuron1)
        println(targetNeuron2)

        assertTrue(conn1.strength > 0, "Connection to correlated neuron should become positive")
        assertTrue(conn2.strength < 0, "Connection to anti-correlated neuron should become negative")
        assertTrue(conn1.strength.absoluteValue <= 1.0, "Connection strength should not exceed 1")
        assertTrue(conn2.strength.absoluteValue <= 1.0, "Connection strength should not exceed -1")
    }
} 