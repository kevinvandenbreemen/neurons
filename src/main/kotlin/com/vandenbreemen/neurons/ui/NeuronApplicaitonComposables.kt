package com.vandenbreemen.neurons.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vandenbreemen.neurons.evolution.model.GeneticWorldState
import com.vandenbreemen.neurons.model.normalizedStrengthUpdate
import com.vandenbreemen.neurons.world.view.NavigationWorldSimulationView
import com.vandenbreemen.neurons.world.viewmodel.NeuralNetApplicationState
import com.vandenbreemen.neurons.world.viewmodel.NeuralNetworkDemoState
import com.vandenbreemen.neurons.world.viewmodel.NeuronInfoState

@Composable
fun NeuralApplicationComposables(state: NeuralNetApplicationState) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("State is a ${state::class.simpleName}")

        when (state) {
            is NeuralNetworkDemoState -> {
                // Display the neural network state
                Text("Neural Network Demo")
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

    // Show neuron details if a neuron is selected, regardless of state
    state.selectedNeuron?.let { neuron ->
        NeuronDetailsUI(neuron) {
            state.selectedNeuron = null
        }
    }
}

@Composable
private fun NeuronDetailsUI(neuron: NeuronInfoState, onCloseClick: () -> Unit) {
    var showDetails by remember { mutableStateOf(true) }

    if (showDetails) {
        AlertDialog(
            onDismissRequest = { showDetails = false },
            title = { Text("Neuron Details") },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth()
                ) {
                    Text("Neuron type: ${neuron.type}")
                    Text("Neuron activation: ${neuron.activation}")
                    Text("Sigmoid Numerator Multiplier:  ${neuron.sigmoidNumeratorMultiplier}")
                    Text("Learning Rate: ${neuron.learningRate}")
                    Text("Weight Calculator: ${neuron.weightCalculatorTypeName}")
                    Text("Connections:")
                    Column {
                        Box(
                            modifier = Modifier
                                .size(200.dp)
                                .padding(16.dp)
                        ) {
                            val measurer = rememberTextMeasurer()

                            Canvas(
                                modifier = Modifier.fillMaxSize()
                                    .background(Color.Black)
                            ) {
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
                                    val color = if (connection.weight > 0) Color.Green.copy(
                                        alpha = kotlin.math.abs(
                                            connection.weight.coerceIn(
                                                0.0,
                                                1.0
                                            )
                                        ).toFloat()
                                    ) else Color.Red.copy(
                                        alpha = kotlin.math.abs(connection.weight.coerceIn(0.0, 1.0)).toFloat()
                                    )
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
                                    val result = measurer.measure(
                                        AnnotatedString(
                                            text = "%.2f".format(connection.weight),
                                            spanStyles = listOf()
                                        ),
                                        style = TextStyle(
                                            fontSize = 12.sp,
                                            color = Color.White
                                        ),
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                    drawText(
                                        result,
                                        topLeft = Offset(
                                            x = targetX - result.size.width / 2,
                                            y = targetY - result.size.height / 2
                                        )
                                    )
                                }
                            }
                        }

                        Text("Activation Function", style = TextStyle(fontSize = 16.sp))
                        Box(
                            modifier = Modifier
                                .size(200.dp)
                                .padding(16.dp)
                        ) {
                            FunctionPlot(
                                startX = -5.0,
                                endX = 5.0,
                                startY = -5.0,
                                endY = 5.0,
                                f = neuron.neuronSigmoidFunction,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Text("Connection Strength Update", style = TextStyle(fontSize = 16.sp))
                        Box(
                            modifier = Modifier
                                .size(200.dp)
                                .padding(16.dp)
                        ) {
                            FunctionPlot(
                                startX = -1.0,
                                endX = 1.0,
                                startY = -1.0,
                                endY = 1.0,
                                f = { x ->
                                    normalizedStrengthUpdate(
                                        sourceActivation = x,
                                        targetActivation = x,
                                        sourceMaxActivation = 1.0,
                                        targetMaxActivation = 1.0,
                                        currentStrength = 0.0,
                                        learningRate = neuron.learningRate
                                    )
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showDetails = false
                    onCloseClick()
                }) {
                    Text("Close")
                }
            }
        )
    }
}