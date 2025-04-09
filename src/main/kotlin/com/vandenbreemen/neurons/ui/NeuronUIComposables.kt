package com.vandenbreemen.neurons.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import com.vandenbreemen.neurons.model.*
import com.vandenbreemen.neurons.world.viewmodel.NeuralNetworkDemoState
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

@Composable
fun NeuronLegendDialog(
    showLegend: Boolean,
    onDismiss: () -> Unit
) {
    if (showLegend) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Neuron Indicators Legend") },
            text = {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Canvas(modifier = Modifier.size(30.dp)) {
                            drawCircle(
                                color = Color.Red,
                                radius = size.width * 0.15f,
                                center = Offset(size.width / 2, size.height / 2)
                            )
                        }
                        Text("Inhibitory Neuron - Reduces activation of connected neurons")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Canvas(modifier = Modifier.size(30.dp)) {
                            drawCircle(
                                color = Color.Green,
                                radius = size.width * 0.15f,
                                center = Offset(size.width / 2, size.height / 2)
                            )
                        }
                        Text("Sine Neuron - Oscillates activation over time")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Canvas(modifier = Modifier.size(30.dp)) {
                            drawCircle(
                                color = Color.Blue,
                                radius = size.width * 0.15f,
                                center = Offset(size.width / 2, size.height / 2)
                            )
                        }
                        Text("Fixed Weight Neuron - Maintains constant connection strength")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Canvas(modifier = Modifier.size(30.dp)) {
                            drawLine(
                                color = Color.Red,
                                start = Offset(size.width * 0.2f, size.height * 0.2f),
                                end = Offset(size.width * 0.8f, size.height * 0.8f),
                                strokeWidth = 2f
                            )
                            drawLine(
                                color = Color.Red,
                                start = Offset(size.width * 0.2f, size.height * 0.8f),
                                end = Offset(size.width * 0.8f, size.height * 0.2f),
                                strokeWidth = 2f
                            )
                        }
                        Text("Dead Neuron - Does nothing, blocks signal flow")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Canvas(modifier = Modifier.size(30.dp)) {
                            drawCircle(
                                color = Color.Green,
                                radius = size.width * 0.15f,
                                center = Offset(size.width / 2, size.height / 2)
                            )
                            // Draw a small arrow
                            drawLine(
                                color = Color.Green,
                                start = Offset(size.width * 0.5f, size.height * 0.5f),
                                end = Offset(size.width * 0.8f, size.height * 0.5f),
                                strokeWidth = 2f
                            )
                        }
                        Text("Relay Neuron - Sends signal in one direction only")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Canvas(modifier = Modifier.size(30.dp)) {
                            val centerX = size.width / 2
                            val centerY = size.height / 2
                            val mSize = size.width * 0.2f
                            drawLine(
                                color = Color.Green,
                                start = Offset(centerX - mSize, centerY + mSize),
                                end = Offset(centerX - mSize / 2, centerY - mSize),
                                strokeWidth = 2f
                            )
                            drawLine(
                                color = Color.Green,
                                start = Offset(centerX - mSize / 2, centerY - mSize),
                                end = Offset(centerX + mSize / 2, centerY + mSize),
                                strokeWidth = 2f
                            )
                            drawLine(
                                color = Color.Green,
                                start = Offset(centerX + mSize / 2, centerY + mSize),
                                end = Offset(centerX + mSize, centerY - mSize),
                                strokeWidth = 2f
                            )
                            drawLine(
                                color = Color.Green,
                                start = Offset(centerX + mSize, centerY - mSize),
                                end = Offset(centerX - mSize, centerY + mSize),
                                strokeWidth = 2f
                            )
                        }
                        Text("Motor Neuron - Provides an action ID for motor control")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Canvas(modifier = Modifier.size(30.dp)) {
                            val centerX = size.width / 2
                            val centerY = size.height / 2
                            val sSize = size.width * 0.2f
                            // Draw the S shape using multiple lines
                            drawLine(
                                color = Color.Blue,
                                start = Offset(centerX - sSize, centerY - sSize),
                                end = Offset(centerX + sSize, centerY - sSize),
                                strokeWidth = 2f
                            )
                            drawLine(
                                color = Color.Blue,
                                start = Offset(centerX + sSize, centerY - sSize),
                                end = Offset(centerX - sSize, centerY),
                                strokeWidth = 2f
                            )
                            drawLine(
                                color = Color.Blue,
                                start = Offset(centerX - sSize, centerY),
                                end = Offset(centerX + sSize, centerY + sSize),
                                strokeWidth = 2f
                            )
                        }
                        Text("Sensory Neuron - Processes specific types of sensory data")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("Close")
                }
            }
        )
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NeuralNetworkDisplay(
    demoState: NeuralNetworkDemoState,
    turnWait: Long = 100,
    showConnections: Boolean = true,
    showActivationColor: Boolean = true,
    iterate: () -> Unit,
    onNeuronClick: ((Neuron) -> Unit)? = null
) {
    val neuralNet = demoState.neuralNet
    var pointerInput by remember { mutableStateOf<PointerInputChange?>(null) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(turnWait)
            iterate()
        }
    }

    Column {
        Text("Turns: ${demoState.currentTurn}")
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .onPointerEvent(PointerEventType.Press) { event ->
                    pointerInput = event.changes.firstOrNull()
                }
        ) {
            val cellWidth = size.width / neuralNet.cols
            val cellHeight = size.height / neuralNet.rows

            // Handle click if we have a pointer input
            val input = pointerInput
            if (input != null) {
                val x = input.position.x
                val y = input.position.y
                val row = (y / cellHeight).toInt()
                val col = (x / cellWidth).toInt()
                if (row in 0 until neuralNet.rows && col in 0 until neuralNet.cols) {
                    println("Clicked on neuron at $row, $col")
                    onNeuronClick?.invoke(neuralNet.getCellAt(row, col))
                }
                pointerInput = null
            }

            for (i in 0 until neuralNet.rows) {
                for (j in 0 until neuralNet.cols) {
                    val neuron = neuralNet.getCellAt(i, j)
                    val activation = neuron.activation
                    val color = if (showActivationColor) {
                        Color(activation.toFloat(), activation.toFloat(), activation.toFloat())
                    } else {
                        Color.Gray // Neutral color when activation coloring is off
                    }
                    drawRect(color, topLeft = Offset(j * cellWidth, i * cellHeight), size = Size(cellWidth, cellHeight))

                    // Draw indicators for special neuron types
                    val centerX = j * cellWidth + cellWidth / 2
                    val centerY = i * cellHeight + cellHeight / 2
                    val dotRadius = minOf(cellWidth, cellHeight) * 0.15f

                    when (neuron) {
                        is InhibitoryNeuron -> {
                            drawCircle(
                                color = Color.Red,
                                radius = dotRadius,
                                center = Offset(centerX, centerY)
                            )
                        }

                        is SineNeuron -> {
                            drawCircle(
                                color = Color.Green,
                                radius = dotRadius,
                                center = Offset(centerX, centerY)
                            )
                        }

                        is FixedWeightNeuron -> {
                            drawCircle(
                                color = Color.Blue,
                                radius = dotRadius,
                                center = Offset(centerX, centerY)
                            )
                        }

                        is DeadNeuron -> {
                            // Draw a red X at the center
                            val xSize = minOf(cellWidth, cellHeight) * 0.2f // Size of the X
                            // Draw first diagonal of X
                            drawLine(
                                color = Color.Red,
                                start = Offset(centerX - xSize, centerY - xSize),
                                end = Offset(centerX + xSize, centerY + xSize),
                                strokeWidth = 2f
                            )
                            // Draw second diagonal of X
                            drawLine(
                                color = Color.Red,
                                start = Offset(centerX - xSize, centerY + xSize),
                                end = Offset(centerX + xSize, centerY - xSize),
                                strokeWidth = 2f
                            )
                        }

                        is MotorNeuron -> {
                            //  Draw a green M at the center of the neuron
                            val mSize = minOf(cellWidth, cellHeight) * 0.2f // Size of the M
                            drawLine(
                                color = Color.Green,
                                start = Offset(centerX - mSize, centerY + mSize),
                                end = Offset(centerX - mSize / 2, centerY - mSize),
                                strokeWidth = 2f
                            )
                            drawLine(
                                color = Color.Green,
                                start = Offset(centerX - mSize / 2, centerY - mSize),
                                end = Offset(centerX + mSize / 2, centerY + mSize),
                                strokeWidth = 2f
                            )
                            drawLine(
                                color = Color.Green,
                                start = Offset(centerX + mSize / 2, centerY + mSize),
                                end = Offset(centerX + mSize, centerY - mSize),
                                strokeWidth = 2f
                            )
                            drawLine(
                                color = Color.Green,
                                start = Offset(centerX + mSize, centerY - mSize),
                                end = Offset(centerX - mSize, centerY + mSize),
                                strokeWidth = 2f
                            )
                        }

                        is SensoryNeuron -> {
                            // Draw a blue S at the center of the neuron
                            val sSize = minOf(cellWidth, cellHeight) * 0.2f // Size of the S
                            // Draw the S shape using multiple lines
                            drawLine(
                                color = Color.Blue,
                                start = Offset(centerX - sSize, centerY - sSize),
                                end = Offset(centerX + sSize, centerY - sSize),
                                strokeWidth = 2f
                            )
                            drawLine(
                                color = Color.Blue,
                                start = Offset(centerX + sSize, centerY - sSize),
                                end = Offset(centerX - sSize, centerY),
                                strokeWidth = 2f
                            )
                            drawLine(
                                color = Color.Blue,
                                start = Offset(centerX - sSize, centerY),
                                end = Offset(centerX + sSize, centerY + sSize),
                                strokeWidth = 2f
                            )
                        }

                        is RelayNeuron -> {
                            // Draw a green circle for the relay neuron
                            drawCircle(
                                color = Color.Green,
                                radius = dotRadius,
                                center = Offset(centerX, centerY)
                            )

                            // Draw arrow to target neuron if it exists
                            val targetNeuron = neuron.getTargetNeuron()
                            if (targetNeuron != null) {
                                // Find target neuron position in the grid
                                var targetRow = -1
                                var targetCol = -1
                                for (r in 0 until neuralNet.rows) {
                                    for (c in 0 until neuralNet.cols) {
                                        if (neuralNet.getCellAt(r, c) == targetNeuron) {
                                            targetRow = r
                                            targetCol = c
                                            break
                                        }
                                    }
                                }

                                if (targetRow != -1 && targetCol != -1) {
                                    val targetCenterX = targetCol * cellWidth + cellWidth / 2
                                    val targetCenterY = targetRow * cellHeight + cellHeight / 2

                                    // Calculate direction vector
                                    val dx = targetCenterX - centerX
                                    val dy = targetCenterY - centerY
                                    val length = kotlin.math.sqrt(dx * dx + dy * dy)
                                    val normalizedDx = dx / length
                                    val normalizedDy = dy / length

                                    // Offset start and end points to avoid overlapping with neurons
                                    val offset = minOf(cellWidth, cellHeight) * 0.2f
                                    val startX = centerX + normalizedDx * offset
                                    val startY = centerY + normalizedDy * offset
                                    val endX = targetCenterX - normalizedDx * offset
                                    val endY = targetCenterY - normalizedDy * offset

                                    // Draw the arrow line
                                    drawLine(
                                        color = Color.Green,
                                        start = Offset(startX, startY),
                                        end = Offset(endX, endY),
                                        strokeWidth = 2f
                                    )

                                    // Draw arrow head
                                    val arrowSize = minOf(cellWidth, cellHeight) * 0.15f
                                    val angle = kotlin.math.atan2(dy, dx)
                                    val arrowAngle1 = angle + Math.PI / 6 // 30 degrees
                                    val arrowAngle2 = angle - Math.PI / 6 // -30 degrees

                                    val arrowX1 = endX - arrowSize * kotlin.math.cos(arrowAngle1)
                                    val arrowY1 = endY - arrowSize * kotlin.math.sin(arrowAngle1)
                                    val arrowX2 = endX - arrowSize * kotlin.math.cos(arrowAngle2)
                                    val arrowY2 = endY - arrowSize * kotlin.math.sin(arrowAngle2)

                                    drawLine(
                                        color = Color.Green,
                                        start = Offset(endX, endY),
                                        end = Offset(arrowX1.toFloat(), arrowY1.toFloat()),
                                        strokeWidth = 2f
                                    )
                                    drawLine(
                                        color = Color.Green,
                                        start = Offset(endX, endY),
                                        end = Offset(arrowX2.toFloat(), arrowY2.toFloat()),
                                        strokeWidth = 2f
                                    )
                                }
                            }
                        }

                        else -> {} // Regular neuron, no indicator needed
                    }

                    if (showConnections) {
                        Direction.entries.forEach { direction ->
                            val strength = neuralNet.getConnectionStrengthFrom(i, j, direction)
                            if (strength != 0.0) {
                                val (dx, dy) = when (direction) {
                                    Direction.UP -> 0 to -1
                                    Direction.DOWN -> 0 to 1
                                    Direction.LEFT -> -1 to 0
                                    Direction.RIGHT -> 1 to 0
                                    Direction.UP_LEFT -> -1 to -1
                                    Direction.UP_RIGHT -> 1 to -1
                                    Direction.DOWN_LEFT -> -1 to 1
                                    Direction.DOWN_RIGHT -> 1 to 1
                                }
                                val startX = j * cellWidth + cellWidth / 2
                                val startY = i * cellHeight + cellHeight / 2
                                val endX = (j + dx) * cellWidth + cellWidth / 2
                                val endY = (i + dy) * cellHeight + cellHeight / 2

                                // Calculate the offset from center for start and end points
                                val offset = minOf(cellWidth, cellHeight) * 0.2f // 20% of cell size
                                val startOffsetX = startX + dx * offset
                                val startOffsetY = startY + dy * offset
                                val endOffsetX = endX - dx * offset
                                val endOffsetY = endY - dy * offset

                                // Calculate the middle point with a perpendicular offset
                                val midX = (startOffsetX + endOffsetX) / 2
                                val midY = (startOffsetY + endOffsetY) / 2
                                val perpOffset = minOf(cellWidth, cellHeight) * 0.3f // 30% of cell size
                                val perpX = midX + dy * perpOffset
                                val perpY = midY - dx * perpOffset

                                // Draw three connected lines
                                val connectionColor = if (strength < 0)
                                    Color.Red.copy(alpha = strength.absoluteValue.toFloat().coerceIn(0f, 1f))
                                else
                                    Color.Green.copy(alpha = strength.toFloat().coerceIn(0f, 1f))

                                // First line from start to middle point
                                drawLine(
                                    color = connectionColor,
                                    start = Offset(startOffsetX, startOffsetY),
                                    end = Offset(perpX, perpY),
                                    strokeWidth = 1f
                                )

                                // Second line from middle point to end
                                drawLine(
                                    color = connectionColor,
                                    start = Offset(perpX, perpY),
                                    end = Offset(endOffsetX, endOffsetY),
                                    strokeWidth = 1f
                                )

                                // Draw arrow at the end
                                val arrowSize = minOf(cellWidth, cellHeight) * 0.15f // Size of the arrow
                                val angle = kotlin.math.atan2(endOffsetY - perpY, endOffsetX - perpX)
                                val arrowAngle1 = angle + Math.PI / 6 // 30 degrees
                                val arrowAngle2 = angle - Math.PI / 6 // -30 degrees

                                // Calculate arrow points
                                val arrowPoint1 = Offset(
                                    (endOffsetX - arrowSize * kotlin.math.cos(arrowAngle1)).toFloat(),
                                    (endOffsetY - arrowSize * kotlin.math.sin(arrowAngle1)).toFloat()
                                )
                                val arrowPoint2 = Offset(
                                    (endOffsetX - arrowSize * kotlin.math.cos(arrowAngle2)).toFloat(),
                                    (endOffsetY - arrowSize * kotlin.math.sin(arrowAngle2)).toFloat()
                                )

                                // Draw arrow lines
                                drawLine(
                                    color = connectionColor,
                                    start = Offset(endOffsetX, endOffsetY),
                                    end = arrowPoint1,
                                    strokeWidth = 1f
                                )
                                drawLine(
                                    color = connectionColor,
                                    start = Offset(endOffsetX, endOffsetY),
                                    end = arrowPoint2,
                                    strokeWidth = 1f
                                )
                            }
                        }
                    }
                }
            }


        }
    }


}