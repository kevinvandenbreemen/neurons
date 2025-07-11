package com.vandenbreemen.neurons.agent

import com.vandenbreemen.neurons.model.*

/**
 * An agent that uses a neural network for decision making
 * @param neuralNet The neural network that powers this agent
 */
class NeuralAgent(
    private val neuralNet: NeuralNet
) {

    private val neuronActionsMap: MutableMap<Neuron, MutableList<(Neuron) -> Unit>> = mutableMapOf()


    /**
     * Causes the agent to perform one iteration of its neural network
     */
    fun iterate() {
        neuralNet.fireAndUpdate()
        neuralNet.updateAllWeights()
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

    /**
     * Finds all pain receptor neurons in the network
     * @return A list of pain receptor neurons in the order they are found in the network
     */
    fun findPainReceptorNeurons(): List<PainReceptorNeuron> {
        val result = mutableListOf<PainReceptorNeuron>()
        for (i in 0 until neuralNet.rows) {
            for (j in 0 until neuralNet.cols) {
                val neuron = neuralNet.getCellAt(i, j)
                if (neuron is PainReceptorNeuron) {
                    result.add(neuron)
                }
            }
        }
        return result
    }

    /**
     * Gets the neural network used by this agent
     */
    fun getNeuralNet(): NeuralNet = neuralNet
} 