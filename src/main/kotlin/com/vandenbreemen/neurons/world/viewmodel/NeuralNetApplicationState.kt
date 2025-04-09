package com.vandenbreemen.neurons.world.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.vandenbreemen.neurons.model.NeuralNet
import com.vandenbreemen.neurons.model.Neuron
import com.vandenbreemen.neurons.provider.GeneticNeuronProvider

open class NeuralNetApplicationState {
    var currentTurn by mutableStateOf(0)
    var selectedNeuron by mutableStateOf<NeuronInfoState?>(null)
    var showConnections by mutableStateOf(false)
    var showActivationColor by mutableStateOf(true)

    open val neuralNet: NeuralNet? = null

    protected open fun doIterate() {

    }

    fun iterate(): NeuralNetApplicationState {
        currentTurn++
        doIterate()
        selectedNeuron?.let {
            selectedNeuron = it.copy() // Copy the state to trigger recomposition
        }
        return this
    }

    fun selectNeuron(neuron: Neuron) {
        selectedNeuron = NeuronInfoState(neuron)
    }
}

class NeuralNetworkDemoState(dim: Int) : NeuralNetApplicationState() {

    override val neuralNet by mutableStateOf<NeuralNet>(
        NeuralNet(
            dim, dim,

            GeneticNeuronProvider.generateGeneticProvider(dim, dim)
        ).also { neuralNet ->
            for (i in 0 until neuralNet.rows) {
                for (j in 0 until neuralNet.cols) {
                    neuralNet.getCellAt(i, j).stimulate(((-5..5).random()).toDouble())
                }
            }
            neuralNet.applyAll()
        })

    override fun doIterate() {
        neuralNet.fireAndUpdate()
        neuralNet.updateAllWeights(0.001)  // Update weights after firing
    }

}

enum class NeuronType {
    REGULAR,
    INHIBITORY,
    SINE,
    FIXED_WEIGHT,
    DEAD,
    SENSORY,
    MOTOR,
    RELAY
}