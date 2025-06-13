package com.vandenbreemen.neurons.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.absoluteValue
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
    f: (Double, Double) -> Double,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Calculate scales
        val xScale = width / (endX - startX)
        val yScale = height / (endY - startY)

        // Draw grid and heat map
        val cellWidth = width / 50  // 50x50 grid
        val cellHeight = height / 50

        // Draw heat map
        for (i in 0..50) {
            for (j in 0..50) {
                val x = startX + (i * (endX - startX) / 50)
                val y = startY + (j * (endY - startY) / 50)
                val value = f(x, y)

                // Convert value to color (blue for negative, red for positive)
                val color = if (value < 0) {
                    Color.Blue.copy(alpha = value.absoluteValue.coerceIn(0.0, 1.0).toFloat())
                } else {
                    Color.Red.copy(alpha = value.coerceIn(0.0, 1.0).toFloat())
                }

                drawRect(
                    color = color,
                    topLeft = Offset(i * cellWidth, j * cellHeight),
                    size = Size(cellWidth, cellHeight)
                )
            }
        }

        // Draw axes
        drawLine(
            color = Color.Gray,
            start = Offset(0f, height),
            end = Offset(width, height),
            strokeWidth = 1f
        )
        drawLine(
            color = Color.Gray,
            start = Offset(0f, 0f),
            end = Offset(0f, height),
            strokeWidth = 1f
        )

        // Draw axis labels
        val xStep = (endX - startX) / 5
        val yStep = (endY - startY) / 5

        // X-axis labels
        for (i in 0..5) {
            val x = startX + i * xStep
            val xPos = (i * width / 5).toFloat()

            // Draw tick mark
            drawLine(
                color = Color.Gray,
                start = Offset(xPos, height - 5f),
                end = Offset(xPos, height + 5f),
                strokeWidth = 1f
            )

            // Draw label
            val text = textMeasurer.measure(
                AnnotatedString("%.1f".format(x)),
                style = TextStyle(fontSize = 8.sp)
            )
            drawText(
                text,
                topLeft = Offset(xPos - text.size.width / 2, height + 10f)
            )
        }

        // Y-axis labels
        for (i in 0..5) {
            val y = startY + i * yStep
            val yPos = height - (i * height / 5).toFloat()

            // Draw tick mark
            drawLine(
                color = Color.Gray,
                start = Offset(-5f, yPos),
                end = Offset(5f, yPos),
                strokeWidth = 1f
            )

            // Draw label
            val text = textMeasurer.measure(
                AnnotatedString("%.1f".format(y)),
                style = TextStyle(fontSize = 8.sp)
            )
            drawText(
                text,
                topLeft = Offset(-text.size.width - 10f, yPos - text.size.height / 2)
            )
        }

        // Draw value labels
        val valueText = textMeasurer.measure(
            AnnotatedString("Source Activation →"),
            style = TextStyle(fontSize = 8.sp)
        )
        drawText(
            valueText,
            topLeft = Offset(width / 2 - valueText.size.width / 2, height + 25f)
        )

        val valueText2 = textMeasurer.measure(
            AnnotatedString("Target Activation ↑"),
            style = TextStyle(fontSize = 8.sp)
        )
        drawText(
            valueText2,
            topLeft = Offset(-valueText2.size.width - 15f, height / 2)
        )
    }
}