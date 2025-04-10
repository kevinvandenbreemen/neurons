package com.vandenbreemen.neurons.model

/**
 * A neuron that remains at 0 activation for a set number of turns,
 * then activates to 1.0 for a single turn before resetting.
 * @param turnsBeforeActivation The number of turns to wait before activating
 * @param weightCalculator The calculator for connection weights
 */
class BlinkerNeuron(
    private val turnsBeforeActivation: Int,
    weightCalculator: ConnectionWeightCalculator = StrengthBasedConnector
) : Neuron(weightCalculator) {
    
    private var turnCount = 0

    override val activation: Double
        get() = if (turnCount == turnsBeforeActivation) 1.0 else 0.0

    override fun applyStimulation() {
        // Increment turn count
        turnCount++

        // If we've reached activation turn, reset the counter
        if (turnCount > turnsBeforeActivation) {
            turnCount = 0
        }

        // Call parent to handle any stimulation
        super.applyStimulation()
    }

    /**
     * Resets the turn counter to 0
     */
    fun reset() {
        turnCount = 0
    }

    /**
     * Gets the current turn count
     */
    fun getTurnCount(): Int = turnCount

    /**
     * Gets the number of turns before activation
     */
    fun getTurnsBeforeActivation(): Int = turnsBeforeActivation
} 