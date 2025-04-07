package com.vandenbreemen.neurons.world.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vandenbreemen.neurons.world.model.World

@Preview
@Composable
fun WorldPreview() {
    val world = World(100, 100).apply {
        // Create some walls for testing
        createWallRectangle(10, 10, 20, 20) // A square room
        createWallLine(30, 30, 70, 70) // A diagonal wall
        createWallLine(30, 70, 70, 30) // Another diagonal wall
        createWallRectangle(80, 80, 90, 90) // Another square room
    }

    WorldView(
        world = world,
        modifier = Modifier.size(400.dp)
    )
} 