package com.vandenbreemen.neurons.world.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vandenbreemen.neurons.world.viewmodel.GeneticWorldState
import com.vandenbreemen.neurons.world.viewmodel.NeuralNetApplicationState
import com.vandenbreemen.neurons.world.viewmodel.NeuralNetworkDemoState

@Composable
fun NeuralApplicationComposables(state: NeuralNetApplicationState) {
    // This is a placeholder for the main application composables.
    // You can add your main application UI here.
    // For example, you might want to include a navigation bar, a sidebar, or other components.
    // This function can be used as the entry point for your Compose UI.

    Column(modifier = Modifier.fillMaxSize()) {
        Text("State is a ${state::class.simpleName}")

        when (state) {
            is NeuralNetworkDemoState -> {
                // Display the neural network state
                Text("Neural Network Demo")
                state.selectedNeuron?.let { neuron ->
                    Column {
                        Text("Neuron type:  ${neuron.type}")
                        Text("Neuron activation: ${neuron.activation}")
                    }
                }

            }
            is GeneticWorldState -> {
                val simulationState = state.navSimulationForDisplay
                simulationState?.let {
                    NavigationWorldSimulationView(simulationState)
                } ?: run {
                    Text("No simulation state available")
                }
            }

            else -> {
                // Handle other states if necessary
                Text("Unknown state")
            }
        }

    }


}