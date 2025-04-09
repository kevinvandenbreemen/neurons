package com.vandenbreemen.neurons.world.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf

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

    /**
     * Iterate (do whatever kind of neural net based computation we're doing)
     */
    fun iterate() {
        updateState {
            iterate()
        }
    }
}