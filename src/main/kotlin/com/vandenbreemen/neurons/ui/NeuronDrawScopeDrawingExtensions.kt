package com.vandenbreemen.neurons.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.vandenbreemen.neurons.model.MotorNeuron
import com.vandenbreemen.neurons.model.MotorNeuronDirections
import com.vandenbreemen.neurons.model.SensoryNeuron
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun DrawScope.drawMotorNeuron(
    cellWidth: Float,
    cellHeight: Float,
    centerX: Float,
    centerY: Float,
    j: Int,
    i: Int,
    neuron: MotorNeuron
) {
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

fun DrawScope.drawSensoryNeuron(
    cellWidth: Float,
    cellHeight: Float,
    centerX: Float,
    centerY: Float,
    j: Int,
    i: Int,
    neuron: SensoryNeuron
) {
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