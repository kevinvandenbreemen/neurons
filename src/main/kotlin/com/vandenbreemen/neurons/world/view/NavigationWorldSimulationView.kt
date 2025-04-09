package com.vandenbreemen.neurons.world.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.vandenbreemen.neurons.world.controller.NavigationWorldSimulation
import kotlinx.coroutines.delay

/**
 * A composable that visualizes a navigation world simulation and automatically iterates it
 * @param simulation The navigation simulation to visualize
 * @param modifier Optional modifier for the canvas
 * @param turnWait The time in milliseconds to wait between simulation steps
 */
@Composable
fun NavigationWorldSimulationView(
    simulation: NavigationWorldSimulation,
    modifier: Modifier = Modifier,
    turnWait: Long = 50L
) {
    var turnCount by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(turnWait)
            simulation.step()
            turnCount++ // Trigger recomposition
        }
    }

    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        val cellWidth = size.width / simulation.world.width
        val cellHeight = size.height / simulation.world.height

        // Draw walls
        for (y in 0 until simulation.world.height) {
            for (x in 0 until simulation.world.width) {
                if (simulation.world.isWall(x, y)) {
                    drawRect(
                        color = Color.Gray,
                        topLeft = Offset(x * cellWidth, y * cellHeight),
                        size = Size(cellWidth, cellHeight)
                    )
                }
            }
        }

        // Draw agents
        simulation.getAgents().forEach { agent ->
            val position = simulation.getAgentPosition(agent)
            if (position != null) {
                // Draw agent as a blue circle
                drawCircle(
                    color = Color.Blue,
                    radius = minOf(cellWidth, cellHeight) * 0.4f,
                    center = Offset(
                        (position.x + 0.5f) * cellWidth,
                        (position.y + 0.5f) * cellHeight
                    )
                )
            }
        }
    }
} 