package com.vandenbreemen.neurons.world.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.vandenbreemen.neurons.world.model.World

/**
 * A composable that visualizes the world
 * @param world The world to visualize
 * @param modifier Optional modifier for the canvas
 */
@Composable
fun WorldView(
    world: World,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        val cellWidth = size.width / world.width
        val cellHeight = size.height / world.height

        // Draw walls
        for (y in 0 until world.height) {
            for (x in 0 until world.width) {
                if (world.isBoundary(x, y)) {
                    drawRect(
                        color = Color.Gray,
                        topLeft = Offset(x * cellWidth, y * cellHeight),
                        size = Size(cellWidth, cellHeight)
                    )
                }
            }
        }
    }
} 