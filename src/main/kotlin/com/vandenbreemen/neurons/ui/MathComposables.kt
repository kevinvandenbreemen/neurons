package com.vandenbreemen.neurons.ui

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
                style = TextStyle(fontSize = 12.sp)
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
                style = TextStyle(fontSize = 12.sp)
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