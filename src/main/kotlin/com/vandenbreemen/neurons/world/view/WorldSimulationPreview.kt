package com.vandenbreemen.neurons.world.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vandenbreemen.neurons.agent.NeuralAgent
import com.vandenbreemen.neurons.model.NeuralNet
import com.vandenbreemen.neurons.world.controller.NavigationWorldSimulation
import com.vandenbreemen.neurons.world.model.AgentPosition
import com.vandenbreemen.neurons.world.model.World

@Preview
@Composable
fun WorldSimulationPreview() {
    // Create a world with random walls
    val world = World.randomWorld(
        width = 100,
        height = 100,
        wallDensity = 0.001,
        minRoomSize = 8,
        maxRoomSize = 20,
        numRooms = 4,
        numRandomWalls = 5
    )

    // Create a simulation with some agents
    val simulation = NavigationWorldSimulation(world).apply {
        // Add some agents at different positions
        addAgent(
            NeuralAgent(NeuralNet(10, 10)),
            AgentPosition(5, 5)
        )
        addAgent(
            NeuralAgent(NeuralNet(10, 10)),
            AgentPosition(25, 25)
        )
        addAgent(
            NeuralAgent(NeuralNet(10, 10)),
            AgentPosition(85, 85)
        )
    }

    WorldSimulationView(
        simulation = simulation,
        modifier = Modifier.size(400.dp)
    )
} 