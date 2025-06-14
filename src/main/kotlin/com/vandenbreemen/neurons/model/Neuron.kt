package com.vandenbreemen.neurons.model

import kotlin.math.absoluteValue
import kotlin.math.exp

open class Neuron(val weightCalculator: ConnectionWeightCalculator = StrengthBasedConnector) {
    internal val connections = mutableListOf<Connection>()

    /**
     * Map of initial connection weights for each direction
     */
    private val initialConnectionWeights = mutableMapOf<Direction, Double>()

    /**
     * Sets the initial connection weight for a specific direction
     * @param direction The direction to set the weight for
     * @param weight The initial weight (should be between 0 and 1)
     */
    fun setInitialConnectionWeight(direction: Direction, weight: Double) {
        require(weight in 0.0..1.0) { "Weight must be between 0 and 1" }
        initialConnectionWeights[direction] = weight
    }

    /**
     * Gets the initial connection weight for a specific direction
     * @param direction The direction to get the weight for
     * @return The initial weight for that direction, or 0.0 if not set
     */
    fun getInitialConnectionWeight(direction: Direction): Double {
        return initialConnectionWeights[direction] ?: 0.0
    }

    /**
     * Current value of this neuron.
     */
    protected var value = 0.0
    open val activation: Double
        get() = value

    private var sigmoidExpDelta = 0.0
    fun setSigmaExpDelta(delta: Double) {
        sigmoidExpDelta = delta
    }

    private var learningRate: Double = 0.0
    val learningRateOverride: Double
        get() = learningRate

    var sigmoidNumerator: Double? = null


    private var sigmoidNumeratorMultiplier = 1.0
    fun setSigmoidNumeratorMultiplier(multiplier: Double) {
        sigmoidNumeratorMultiplier = multiplier
    }

    val maxActivationValue: Double
        get() = sigmoidNumerator ?: sigmoidNumeratorMultiplier

    /**
     * Current amount of stimulation this neuron has received.  Note that this is not the same as the value of the neuron.
     */
    protected var stimulationValue = 0.0

    fun connect(neuron: Neuron, strength: Double = 1.0) {
        if (strength.absoluteValue > 1.0f) throw IllegalArgumentException("Strength must be between -1 and 1")
        //  Don't allow more than one connection to same neuron
        if (connections.any { it.neuron == neuron }) return
        connections.add(Connection(neuron, strength))
    }

    fun setLearningRate(learningRate: Double) {
        this.learningRate = learningRate
    }

    open fun stimulate(input: Double) {
        stimulationValue = (stimulationValue + input)
    }

    /**
     * Update the value to be the total stimulation received
     */
    open fun applyStimulation() {
        value = sigmoid(stimulationValue)
        stimulationValue = 0.0
    }

    fun sigmoid(x: Double): Double {
        return (sigmoidNumerator
            ?: sigmoidNumeratorMultiplier) / (1 + exp(-(-sigmoidNumeratorMultiplier + (x * sigmoidExpDelta))))
    }

    /**
     * Updates all connection weights based on Hebbian learning.
     * The weight change is determined by the correlation between this neuron's activation
     * and each target neuron's activation.
     */
    open fun updateAllConnectionWeights() {
        val updatedConnections = connections.map { connection ->
            val newStrength = weightCalculator.calculateWeight(
                this,
                connection.neuron,
                connection.strength,
                this.learningRate
            )
            Connection(connection.neuron, newStrength)
        }

        // Replace all connections with their updated versions
        connections.clear()
        connections.addAll(updatedConnections)
    }

    open fun fire() {
        connections.forEach { it.neuron.stimulate(it.weight * value) }
    }

    override fun toString(): String {
        return "Neuron(value=$value, connections=$connections)"
    }
}

data class Connection(
    val neuron: Neuron,
    val strength: Double,
    private val sigmoidCalculator: (Double) -> Double = { x -> 1 / (1 + exp(-x)) }
) {

    val weight: Double
        get() = strength

    override fun toString(): String {
        return "Connection to neuron:  strength=$strength"
    }
}