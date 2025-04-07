package com.vandenbreemen.neurons.agent

import com.vandenbreemen.neurons.model.MotorNeuron
import com.vandenbreemen.neurons.model.NeuralNet
import com.vandenbreemen.neurons.model.Neuron
import com.vandenbreemen.neurons.model.SensoryNeuron

/**
 * An agent that manages a neural network and provides methods for iteration and learning
 * @param neuralNet The neural network to be managed by this agent
 * @param learningRate The rate at which the network learns and updates its weights
 */
class NeuralAgent(
    private val neuralNet: NeuralNet,
    private val learningRate: Double
) {

    private val neuronActionsMap: MutableMap<Neuron, MutableList<(Neuron) -> Unit>> = mutableMapOf()


    /**
     * Performs one iteration of the neural network, including firing, updating, and weight adjustments
     */
    fun iterate() {
        neuralNet.fireAndUpdate()
        neuralNet.updateAllWeights(learningRate)
        neuronActionsMap.entries.forEach {
            it.value.forEach { action ->
                action(it.key)
            }
        }
    }

    /**
     * Adds an action to be performed on a specific neuron during the iteration
     * @param neuron The neuron to which the action will be added
     * @param action The action to be performed on the neuron
     */
    fun addNeuronAction(neuron: Neuron, action: (Neuron) -> Unit) {
        neuronActionsMap.computeIfAbsent(neuron) { mutableListOf() }.add(action)
    }

    /**
     * Finds all motor neurons in the network that match the given filter function
     * @param filter A function that takes an action ID and returns true if the motor neuron should be included
     * @return A list of motor neurons that match the filter, in the order they are found in the network
     */
    fun findMotorNeurons(filter: (Byte) -> Boolean): List<MotorNeuron> {
        val result = mutableListOf<MotorNeuron>()
        for (i in 0 until neuralNet.rows) {
            for (j in 0 until neuralNet.cols) {
                val neuron = neuralNet.getCellAt(i, j)
                if (neuron is MotorNeuron && filter(neuron.actionId)) {
                    result.add(neuron)
                }
            }
        }
        return result
    }

    /**
     * Finds all sensory neurons in the network that match the given filter function
     * @param filter A function that takes a sensor ID and returns true if the sensory neuron should be included
     * @return A list of sensory neurons that match the filter, in the order they are found in the network
     */
    fun findSensoryNeurons(filter: (Byte) -> Boolean): List<SensoryNeuron> {
        val result = mutableListOf<SensoryNeuron>()
        for (i in 0 until neuralNet.rows) {
            for (j in 0 until neuralNet.cols) {
                val neuron = neuralNet.getCellAt(i, j)
                if (neuron is SensoryNeuron && filter(neuron.sensorId)) {
                    result.add(neuron)
                }
            }
        }
        return result
    }
} 