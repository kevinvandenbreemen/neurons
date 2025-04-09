package com.vandenbreemen.neurons.world.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

open class NeuralNetApplicationState {
    var isSimulationRunning by mutableStateOf(false)
    var currentTurn by mutableStateOf(0)
    var selectedNeuronType by mutableStateOf<NeuronType?>(null)
    var showConnections by mutableStateOf(true)
    var showActivationColor by mutableStateOf(true)
}

class NeuralNetworkDemoState : NeuralNetApplicationState() {

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