package com.vandenbreemen.neurons.world.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.vandenbreemen.neurons.model.Neuron

class NeuronApplicationViewModel() {
    private val _state = mutableStateOf(NeuralNetApplicationState())
    val state: NeuralNetApplicationState by _state

    private fun updateState(update: NeuralNetApplicationState.() -> Unit) {
        _state.value = _state.value.apply(update)
    }

    fun switchToApplication(neuralNetApplicationState: NeuralNetApplicationState) {
        _state.value = neuralNetApplicationState
    }

    fun toggleActivationColor() {
        updateState {
            showActivationColor = !showActivationColor
        }
    }

    fun toggleShowConnections() {
        updateState {
            showConnections = !showConnections
        }
    }

    fun onSelectNeuron(neuron: Neuron) {
        updateState {
            selectNeuron(neuron)
        }
    }

    /**
     * Iterate (do whatever kind of neural net based computation we're doing)
     */
    fun iterate() {
        updateState {
            iterate()
        }
    }
}