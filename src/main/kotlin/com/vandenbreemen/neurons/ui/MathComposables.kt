package com.vandenbreemen.neurons.ui

import androidx.compose.animation.core.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.exp

@Composable
fun FunctionPlot(
    startX: Double,
    endX: Double,
    startY: Double,
    endY: Double,
    f: (Double) -> Double,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Calculate scales
        val xScale = width / (endX - startX)
        val yScale = height / (endY - startY)

        // Calculate zero points
        val zeroX = (-startX * xScale).toFloat()
        val zeroY = height - (-startY * yScale).toFloat()

        // Draw axes
        drawLine(
            color = Color.Gray,
            start = Offset(0f, zeroY),
            end = Offset(width, zeroY),
            strokeWidth = 1f
        )
        drawLine(
            color = Color.Gray,
            start = Offset(zeroX, 0f),
            end = Offset(zeroX, height),
            strokeWidth = 1f
        )

        // Draw axis labels
        val xStep = if (endX != startX) (endX - startX) / 10 else 1.0
        val yStep = if (endY != startY) (endY - startY) / 10 else 1.0

        // X-axis labels
        var currentX = startX
        while (currentX <= endX) {
            val xPos = ((currentX - startX) * xScale).toFloat()
            // Draw tick mark
            drawLine(
                color = Color.Gray,
                start = Offset(xPos, zeroY - 5f),
                end = Offset(xPos, zeroY + 5f),
                strokeWidth = 1f
            )
            // Draw label
            val text = textMeasurer.measure(
                AnnotatedString("%.1f".format(currentX)),
                style = TextStyle(fontSize = 8.sp)
            )
            drawText(
                text,
                topLeft = Offset(xPos - text.size.width / 2, zeroY + 10f)
            )
            currentX += xStep
        }

        // Y-axis labels
        var currentY = startY
        while (currentY <= endY) {
            val yPos = height - ((currentY - startY) * yScale).toFloat()
            // Draw tick mark
            drawLine(
                color = Color.Gray,
                start = Offset(zeroX - 5f, yPos),
                end = Offset(zeroX + 5f, yPos),
                strokeWidth = 1f
            )
            // Draw label
            val text = textMeasurer.measure(
                AnnotatedString("%.1f".format(currentY)),
                style = TextStyle(fontSize = 8.sp)
            )
            drawText(
                text,
                topLeft = Offset(zeroX - text.size.width - 10f, yPos - text.size.height / 2)
            )
            currentY += yStep
        }

        // Draw function
        val points = (0 until width.toInt()).map { x ->
            val xValue = startX + x / xScale
            val yValue = f(xValue)
            val y = height - ((yValue - startY) * yScale).toFloat()
            Offset(x.toFloat(), y)
        }

        // Draw function as a series of lines
        for (i in 0 until points.size - 1) {
            drawLine(
                color = Color.Blue,
                start = points[i],
                end = points[i + 1],
                strokeWidth = 1f
            )
        }
    }
}

@Preview
@Composable
fun SigmoidFunctionPlotPreview() {
    FunctionPlot(
        startX = -5.0,
        endX = 5.0,
        startY = 0.0,
        endY = 1.0,
        f = { x -> 1.0 / (1.0 + exp(-x)) },
        modifier = Modifier.size(400.dp)
    )
}

@Composable
fun Function3DPlot(
    startX: Double,
    endX: Double,
    startY: Double,
    endY: Double,
    numSteps: Int = 20,
    f: (Double, Double) -> Double,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    // Create an infinite animation for rotation
    val infiniteTransition = rememberInfiniteTransition()
    val rotation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // 3D transformation parameters
        val elevation = 45.0 // degrees
        val zScale = 0.3 // Scale factor for the z-axis (function values)

        // Calculate ranges
        val xRange = endX - startX
        val yRange = endY - startY

        // Calculate scale to fit the plot in the available space
        // We use 0.8 to leave some margin around the edges
        val scale = 0.8 * minOf(
            width / xRange,
            height / yRange
        )

        // Convert angles to radians
        val elevationRad = elevation * Math.PI / 180.0
        val rotationRad = (rotation.value * Math.PI / 180.0)

        // Calculate the center of the plot
        val centerX = width / 2
        val centerY = height / 2

        // Function to project 3D point to 2D
        fun project3D(x: Double, y: Double, z: Double): Offset {
            // Apply rotation around Z axis
            val rotatedX = x * Math.cos(rotationRad) - y * Math.sin(rotationRad)
            val rotatedY = x * Math.sin(rotationRad) + y * Math.cos(rotationRad)

            // Apply elevation
            val elevatedY = rotatedY * Math.cos(elevationRad) - z * Math.sin(elevationRad)
            val elevatedZ = rotatedY * Math.sin(elevationRad) + z * Math.cos(elevationRad)

            // Project to 2D
            val projectedX = centerX + (rotatedX * scale)
            val projectedY = centerY - (elevatedY * scale)

            return Offset(projectedX.toFloat(), projectedY.toFloat())
        }

        // Draw the surface
        val gridSize = 20
        val stepX = (endX - startX) / numSteps.toDouble()
        val stepY = (endY - startY) / numSteps.toDouble()

        // Draw the surface grid
        for (i in 0..gridSize) {
            for (j in 0..gridSize) {
                val x = startX + i * stepX
                val y = startY + j * stepY
                val z = f(x, y) * zScale

                val point = project3D(x, y, z)

                // Draw vertical lines
                if (i < gridSize) {
                    val nextX = startX + (i + 1) * stepX
                    val nextZ = f(nextX, y) * zScale
                    val nextPoint = project3D(nextX, y, nextZ)
                    drawLine(
                        color = Color.Gray.copy(alpha = 0.5f),
                        start = point,
                        end = nextPoint,
                        strokeWidth = 1f
                    )
                }

                // Draw horizontal lines
                if (j < gridSize) {
                    val nextY = startY + (j + 1) * stepY
                    val nextZ = f(x, nextY) * zScale
                    val nextPoint = project3D(x, nextY, nextZ)
                    drawLine(
                        color = Color.Gray.copy(alpha = 0.5f),
                        start = point,
                        end = nextPoint,
                        strokeWidth = 1f
                    )
                }

                // Draw points
                drawCircle(
                    color = if (z > 0) Color.Red.copy(alpha = 0.3f) else Color.Blue.copy(alpha = 0.3f),
                    radius = 1f,
                    center = point
                )
            }
        }

        // Draw axes
        val origin = project3D(0.0, 0.0, 0.0)
        val xAxis = project3D(xRange, 0.0, 0.0)
        val yAxis = project3D(0.0, yRange, 0.0)
        val zAxis = project3D(0.0, 0.0, 1.0)

        // Draw axis lines
        drawLine(color = Color.Red, start = origin, end = xAxis, strokeWidth = 2f)
        drawLine(color = Color.Green, start = origin, end = yAxis, strokeWidth = 2f)
        drawLine(color = Color.Blue, start = origin, end = zAxis, strokeWidth = 2f)

        // Draw axis labels
        val xLabel = textMeasurer.measure(
            AnnotatedString("X"),
            style = TextStyle(fontSize = 8.sp, color = Color.Red)
        )
        drawText(xLabel, topLeft = Offset(xAxis.x, xAxis.y - xLabel.size.height))

        val yLabel = textMeasurer.measure(
            AnnotatedString("Y"),
            style = TextStyle(fontSize = 8.sp, color = Color.Green)
        )
        drawText(yLabel, topLeft = Offset(yAxis.x, yAxis.y - yLabel.size.height))

        val zLabel = textMeasurer.measure(
            AnnotatedString("Z"),
            style = TextStyle(fontSize = 8.sp, color = Color.Blue)
        )
        drawText(zLabel, topLeft = Offset(zAxis.x, zAxis.y - zLabel.size.height))
    }
}