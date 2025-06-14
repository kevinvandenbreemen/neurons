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
import com.vandenbreemen.neurons.model.Direction
import com.vandenbreemen.neurons.world.viewmodel.NeuralNetApplicationState
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.sin

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
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Canvas(modifier = Modifier.size(30.dp)) {
                            val centerX = size.width / 2
                            val centerY = size.height / 2
                            val chevronSize = size.width * 0.3f
                            // Draw a chevron pointing right
                            drawLine(
                                color = Color.Magenta,
                                start = Offset(centerX - chevronSize, centerY),
                                end = Offset(centerX, centerY - chevronSize),
                                strokeWidth = 2f
                            )
                            drawLine(
                                color = Color.Magenta,
                                start = Offset(centerX, centerY - chevronSize),
                                end = Offset(centerX + chevronSize, centerY),
                                strokeWidth = 2f
                            )
                            drawLine(
                                color = Color.Magenta,
                                start = Offset(centerX + chevronSize, centerY),
                                end = Offset(centerX, centerY + chevronSize),
                                strokeWidth = 2f
                            )
                            drawLine(
                                color = Color.Magenta,
                                start = Offset(centerX, centerY + chevronSize),
                                end = Offset(centerX - chevronSize, centerY),
                                strokeWidth = 2f
                            )
                        }
                        Text("Blinker Neuron - Activates periodically after a set number of turns")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Canvas(modifier = Modifier.size(30.dp)) {
                            val centerX = size.width / 2
                            val centerY = size.height / 2
                            val tSize = size.width * 0.2f
                            // Draw the horizontal bar of the T
                            drawLine(
                                color = Color.Yellow,
                                start = Offset(centerX - tSize, centerY),
                                end = Offset(centerX + tSize, centerY),
                                strokeWidth = 2f
                            )
                            // Draw the vertical bar of the T
                            drawLine(
                                color = Color.Yellow,
                                start = Offset(centerX, centerY - tSize),
                                end = Offset(centerX, centerY + tSize),
                                strokeWidth = 2f
                            )
                        }
                        Text("Threshold Neuron - Only fires when stimulation reaches a certain threshold")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Canvas(modifier = Modifier.size(30.dp)) {
                            val centerX = size.width / 2
                            val centerY = size.height / 2
                            val pSize = size.width * 0.2f
                            // Draw a P shape for pain receptor
                            drawLine(
                                color = Color.Red,
                                start = Offset(centerX - pSize, centerY - pSize),
                                end = Offset(centerX - pSize, centerY + pSize),
                                strokeWidth = 2f
                            )
                            drawLine(
                                color = Color.Red,
                                start = Offset(centerX - pSize, centerY - pSize),
                                end = Offset(centerX + pSize, centerY - pSize),
                                strokeWidth = 2f
                            )
                            drawLine(
                                color = Color.Red,
                                start = Offset(centerX - pSize, centerY),
                                end = Offset(centerX + pSize, centerY),
                                strokeWidth = 2f
                            )
                            drawLine(
                                color = Color.Red,
                                start = Offset(centerX + pSize, centerY - pSize),
                                end = Offset(centerX + pSize, centerY),
                                strokeWidth = 2f
                            )
                        }
                        Text("Pain Receptor Neuron - Sends a signal of 1.0 when stimulated by the environment")
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
    demoState: NeuralNetApplicationState,
    turnWait: Long = 100,
    showConnections: Boolean = true,
    showActivationColor: Boolean = true,
    iterate: () -> Unit,
    onNeuronClick: ((Neuron) -> Unit)? = null
) {
    val neuralNet = demoState.neuralNet

    if (neuralNet == null) {
        Text("Neural Network not initialized")
        return
    }

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

                        is MotorNeuron -> {
                            //  Draw a green M at the center of the neuron
                            val mSize = minOf(cellWidth, cellHeight) * 0.15f // Size of the M, reduced from 0.2f
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

                            // Draw directional arrow in top right corner
                            val arrowSize = minOf(cellWidth, cellHeight) * 0.15f
                            val arrowX = j * cellWidth + cellWidth * 0.8f
                            val arrowY = i * cellHeight + cellHeight * 0.2f

                            // Determine direction based on actionId
                            val angle = when {
                                neuron.actionId in MotorNeuronDirections.NORTH_START..MotorNeuronDirections.NORTH_END -> PI / 2
                                neuron.actionId in MotorNeuronDirections.NORTHEAST_START..MotorNeuronDirections.NORTHEAST_END -> PI / 4
                                neuron.actionId in MotorNeuronDirections.EAST_START..MotorNeuronDirections.EAST_END -> 0.0
                                neuron.actionId in MotorNeuronDirections.SOUTHEAST_START..MotorNeuronDirections.SOUTHEAST_END -> -PI / 4
                                neuron.actionId in MotorNeuronDirections.SOUTH_START..MotorNeuronDirections.SOUTH_END -> -PI / 2
                                neuron.actionId in MotorNeuronDirections.SOUTHWEST_START..MotorNeuronDirections.SOUTHWEST_END -> -3 * PI / 4
                                neuron.actionId in MotorNeuronDirections.WEST_START..MotorNeuronDirections.WEST_END -> PI
                                neuron.actionId in MotorNeuronDirections.NORTHWEST_START..MotorNeuronDirections.NORTHWEST_END -> 3 * PI / 4
                                else -> 0.0
                            }

                            // Draw arrow shaft
                            drawLine(
                                color = Color.Green,
                                start = Offset(arrowX, arrowY),
                                end = Offset(
                                    arrowX + (arrowSize * cos(angle)).toFloat(),
                                    arrowY + (arrowSize * sin(angle)).toFloat()
                                ),
                                strokeWidth = 2f
                            )

                            // Draw arrow head
                            val arrowHeadSize = arrowSize * 0.4f
                            val arrowHeadAngle1 = angle + PI / 6 // 30 degrees
                            val arrowHeadAngle2 = angle - PI / 6 // -30 degrees

                            val arrowEndX = arrowX + (arrowSize * cos(angle)).toFloat()
                            val arrowEndY = arrowY + (arrowSize * sin(angle)).toFloat()

                            drawLine(
                                color = Color.Green,
                                start = Offset(arrowEndX, arrowEndY),
                                end = Offset(
                                    arrowEndX - (arrowHeadSize * cos(arrowHeadAngle1)).toFloat(),
                                    arrowEndY - (arrowHeadSize * sin(arrowHeadAngle1)).toFloat()
                                ),
                                strokeWidth = 2f
                            )

                            drawLine(
                                color = Color.Green,
                                start = Offset(arrowEndX, arrowEndY),
                                end = Offset(
                                    arrowEndX - (arrowHeadSize * cos(arrowHeadAngle2)).toFloat(),
                                    arrowEndY - (arrowHeadSize * sin(arrowHeadAngle2)).toFloat()
                                ),
                                strokeWidth = 2f
                            )
                        }

                        is SensoryNeuron -> {
                            // Draw a blue S at the center of the neuron
                            val sSize = minOf(cellWidth, cellHeight) * 0.15f // Size of the S, reduced from 0.2f
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

                            // Draw directional arrow in top right corner
                            val arrowSize = minOf(cellWidth, cellHeight) * 0.15f
                            val arrowX = j * cellWidth + cellWidth * 0.8f
                            val arrowY = i * cellHeight + cellHeight * 0.2f

                            // Determine direction based on sensorId
                            val angle = when {
                                neuron.sensorId in MotorNeuronDirections.NORTH_START..MotorNeuronDirections.NORTH_END -> PI / 2
                                neuron.sensorId in MotorNeuronDirections.NORTHEAST_START..MotorNeuronDirections.NORTHEAST_END -> PI / 4
                                neuron.sensorId in MotorNeuronDirections.EAST_START..MotorNeuronDirections.EAST_END -> 0.0
                                neuron.sensorId in MotorNeuronDirections.SOUTHEAST_START..MotorNeuronDirections.SOUTHEAST_END -> -PI / 4
                                neuron.sensorId in MotorNeuronDirections.SOUTH_START..MotorNeuronDirections.SOUTH_END -> -PI / 2
                                neuron.sensorId in MotorNeuronDirections.SOUTHWEST_START..MotorNeuronDirections.SOUTHWEST_END -> -3 * PI / 4
                                neuron.sensorId in MotorNeuronDirections.WEST_START..MotorNeuronDirections.WEST_END -> PI
                                neuron.sensorId in MotorNeuronDirections.NORTHWEST_START..MotorNeuronDirections.NORTHWEST_END -> 3 * PI / 4
                                else -> 0.0
                            }

                            // Draw arrow shaft
                            drawLine(
                                color = Color.Blue,
                                start = Offset(arrowX, arrowY),
                                end = Offset(
                                    arrowX + (arrowSize * cos(angle)).toFloat(),
                                    arrowY + (arrowSize * sin(angle)).toFloat()
                                ),
                                strokeWidth = 2f
                            )

                            // Draw arrow head
                            val arrowHeadSize = arrowSize * 0.4f
                            val arrowHeadAngle1 = angle + PI / 6 // 30 degrees
                            val arrowHeadAngle2 = angle - PI / 6 // -30 degrees

                            val arrowEndX = arrowX + (arrowSize * cos(angle)).toFloat()
                            val arrowEndY = arrowY + (arrowSize * sin(angle)).toFloat()

                            drawLine(
                                color = Color.Blue,
                                start = Offset(arrowEndX, arrowEndY),
                                end = Offset(
                                    arrowEndX - (arrowHeadSize * cos(arrowHeadAngle1)).toFloat(),
                                    arrowEndY - (arrowHeadSize * sin(arrowHeadAngle1)).toFloat()
                                ),
                                strokeWidth = 2f
                            )

                            drawLine(
                                color = Color.Blue,
                                start = Offset(arrowEndX, arrowEndY),
                                end = Offset(
                                    arrowEndX - (arrowHeadSize * cos(arrowHeadAngle2)).toFloat(),
                                    arrowEndY - (arrowHeadSize * sin(arrowHeadAngle2)).toFloat()
                                ),
                                strokeWidth = 2f
                            )
                        }

                        is BlinkerNeuron -> {
                            // Draw a magenta chevron at the center of the neuron
                            val chevronSize = minOf(cellWidth, cellHeight) * 0.3f
                            // Draw a chevron pointing right
                            drawLine(
                                color = Color.Magenta,
                                start = Offset(centerX - chevronSize, centerY),
                                end = Offset(centerX, centerY - chevronSize),
                                strokeWidth = 2f
                            )
                            drawLine(
                                color = Color.Magenta,
                                start = Offset(centerX, centerY - chevronSize),
                                end = Offset(centerX + chevronSize, centerY),
                                strokeWidth = 2f
                            )
                            drawLine(
                                color = Color.Magenta,
                                start = Offset(centerX + chevronSize, centerY),
                                end = Offset(centerX, centerY + chevronSize),
                                strokeWidth = 2f
                            )
                            drawLine(
                                color = Color.Magenta,
                                start = Offset(centerX, centerY + chevronSize),
                                end = Offset(centerX - chevronSize, centerY),
                                strokeWidth = 2f
                            )
                        }

                        is PainReceptorNeuron -> {
                            // Draw a red P at the center of the neuron
                            val pSize = minOf(cellWidth, cellHeight) * 0.2f // Size of the P
                            // Draw a P shape for pain receptor
                            drawLine(
                                color = Color.Red,
                                start = Offset(centerX - pSize, centerY - pSize),
                                end = Offset(centerX - pSize, centerY + pSize),
                                strokeWidth = 2f
                            )
                            drawLine(
                                color = Color.Red,
                                start = Offset(centerX - pSize, centerY - pSize),
                                end = Offset(centerX + pSize, centerY - pSize),
                                strokeWidth = 2f
                            )
                            drawLine(
                                color = Color.Red,
                                start = Offset(centerX - pSize, centerY),
                                end = Offset(centerX + pSize, centerY),
                                strokeWidth = 2f
                            )
                            drawLine(
                                color = Color.Red,
                                start = Offset(centerX + pSize, centerY - pSize),
                                end = Offset(centerX + pSize, centerY),
                                strokeWidth = 2f
                            )
                        }

                        else -> {
                            // Draw a dot in the top left corner based on sigmoidNumeratorMultiplier
                            val dotX = j * cellWidth + cellWidth * 0.2f
                            val dotY = i * cellHeight + cellHeight * 0.2f
                            val dotColor = if (neuron.maxActivationValue > 0) {
                                // Green for positive values, brighter for higher values
                                Color(0f, neuron.maxActivationValue.coerceIn(0.0, 1.0).toFloat(), 0f)
                            } else {
                                // Red for negative values, brighter for lower values
                                Color(neuron.maxActivationValue.absoluteValue.coerceIn(0.0, 1.0).toFloat(), 0f, 0f)
                            }
                            drawCircle(
                                color = dotColor,
                                radius = dotRadius * 0.5f,
                                center = Offset(dotX, dotY)
                            )
                        }


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