package com.vandenbreemen.neurons.world.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf

class NeuronApplicationViewModel {
    private val _state = mutableStateOf(NeuralNetApplicationState())
    val state: NeuralNetApplicationState by _state

    fun updateState(update: NeuralNetApplicationState.() -> Unit) {
        _state.value = _state.value.apply(update)
    }
}