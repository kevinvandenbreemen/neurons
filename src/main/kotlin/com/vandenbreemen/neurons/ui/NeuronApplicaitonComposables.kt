package com.vandenbreemen.neurons.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vandenbreemen.neurons.world.view.NavigationWorldSimulationView
import com.vandenbreemen.neurons.world.viewmodel.GeneticWorldState
import com.vandenbreemen.neurons.world.viewmodel.NeuralNetApplicationState
import com.vandenbreemen.neurons.world.viewmodel.NeuralNetworkDemoState
import com.vandenbreemen.neurons.world.viewmodel.NeuronInfoState

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
                    NeuronDetailsUI(neuron)
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

@Composable
private fun NeuronDetailsUI(neuron: NeuronInfoState) {
    Column {
        Text("Neuron type: ${neuron.type}")
        Text("Neuron activation: ${neuron.activation}")

        neuron.threshold?.let { threshold ->
            Text("Threshold: $threshold")
        }

        Text("Connections:")
        Box(
            modifier = Modifier
                .size(200.dp)
                .padding(16.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerX = size.width / 2
                val centerY = size.height / 2
                val radius = minOf(size.width, size.height) * 0.4f

                // Draw the center neuron
                drawCircle(
                    color = Color.Gray,
                    radius = 10f,
                    center = Offset(centerX, centerY)
                )

                // Draw connections in a circle around the center
                neuron.connections.forEachIndexed { index, connection ->
                    val angle = (index * 2 * Math.PI / neuron.connections.size).toFloat()
                    val targetX = centerX + radius * kotlin.math.cos(angle)
                    val targetY = centerY + radius * kotlin.math.sin(angle)

                    // Draw connection line
                    val color = if (connection.weight > 0) Color.Green else Color.Red
                    drawLine(
                        color = color.copy(alpha = kotlin.math.abs(connection.weight).toFloat()),
                        start = Offset(centerX, centerY),
                        end = Offset(targetX, targetY),
                        strokeWidth = 2f
                    )

                    // Draw target neuron
                    drawCircle(
                        color = Color.Gray,
                        radius = 8f,
                        center = Offset(targetX, targetY)
                    )

                    // Draw weight value

                }
            }
        }
    }
}