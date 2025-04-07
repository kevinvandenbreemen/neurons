package com.vandenbreemen.neurons.world.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vandenbreemen.neurons.agent.NeuralAgent
import com.vandenbreemen.neurons.model.NeuralNet
import com.vandenbreemen.neurons.world.AgentPosition
import com.vandenbreemen.neurons.world.World
import com.vandenbreemen.neurons.world.WorldSimulation

@Preview
@Composable
fun WorldSimulationPreview() {
    // Create a world with some walls
    val world = World(100, 100).apply {
        createWallRectangle(10, 10, 20, 20) // A square room
        createWallLine(30, 30, 70, 70) // A diagonal wall
        createWallLine(30, 70, 70, 30) // Another diagonal wall
        createWallRectangle(80, 80, 90, 90) // Another square room
    }

    // Create a simulation with some agents
    val simulation = WorldSimulation(world).apply {
        // Add some agents at different positions
        addAgent(
            NeuralAgent(NeuralNet(10, 10), 0.1),
            AgentPosition(5, 5)
        )
        addAgent(
            NeuralAgent(NeuralNet(10, 10), 0.1),
            AgentPosition(25, 25)
        )
        addAgent(
            NeuralAgent(NeuralNet(10, 10), 0.1),
            AgentPosition(85, 85)
        )
    }

    WorldSimulationView(
        simulation = simulation,
        modifier = Modifier.size(400.dp)
    )
} 