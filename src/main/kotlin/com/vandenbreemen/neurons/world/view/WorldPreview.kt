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
    val world = World.randomWorld(
        width = 100,
        height = 100,
        wallDensity = 0.01,
        minRoomSize = 8,
        maxRoomSize = 20,
        numRooms = 5,
        numRandomWalls = 2
    )

    WorldView(
        world = world,
        modifier = Modifier.size(400.dp)
    )
} 