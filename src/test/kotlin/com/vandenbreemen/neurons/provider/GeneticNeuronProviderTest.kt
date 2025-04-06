package com.vandenbreemen.neurons.provider

import com.vandenbreemen.neurons.model.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GeneticNeuronProviderTest {

    @Test
    fun `should create MotorNeuron with correct actionId`() {
        // Given
        val actionId: Byte = 0x42
        // Create a gene with:
        // - bits 0-3: weight calculator type (0)
        // - bits 4-7: neuron type (6 for MotorNeuron)
        // - bits 11-18: actionId (0x42)
        val gene = (6L shl 4) or (actionId.toLong() shl 11)
        val provider = GeneticNeuronProvider(longArrayOf(gene))

        // When
        val neuron = provider.getNeuron()

        // Then
        assertTrue(neuron is MotorNeuron, "Should create a MotorNeuron")
        assertEquals(actionId, (neuron as MotorNeuron).actionId, "Action ID should match gene bits 11-18")
    }

    @Test
    fun `should create different neuron types based on gene`() {
        // Given
        val genes = longArrayOf(
            (0L shl 4), // Regular neuron
            (1L shl 4), // Inhibitory neuron
            (2L shl 4), // Sine neuron
            (3L shl 4), // Fixed weight neuron
            (4L shl 4), // Relay neuron
            (5L shl 4), // Dead neuron
            (6L shl 4) or (0x42L shl 11) // Motor neuron with actionId 0x42
        )
        val provider = GeneticNeuronProvider(genes)

        // When/Then
        assertTrue(provider.getNeuron() is Neuron, "Should create regular neuron")
        assertTrue(provider.getNeuron() is InhibitoryNeuron, "Should create inhibitory neuron")
        assertTrue(provider.getNeuron() is SineNeuron, "Should create sine neuron")
        assertTrue(provider.getNeuron() is FixedWeightNeuron, "Should create fixed weight neuron")
        assertTrue(provider.getNeuron() is RelayNeuron, "Should create relay neuron")
        assertTrue(provider.getNeuron() is DeadNeuron, "Should create dead neuron")
        val motorNeuron = provider.getNeuron()
        assertTrue(motorNeuron is MotorNeuron, "Should create motor neuron")
        assertEquals(0x42.toByte(), motorNeuron.actionId, "Motor neuron should have correct actionId")
    }
} 