package com.vandenbreemen.neurons.world.view

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.vandenbreemen.neurons.world.viewmodel.NeuralNetApplicationState

@Composable
fun NeuralApplicationComposables(state: NeuralNetApplicationState) {
    // This is a placeholder for the main application composables.
    // You can add your main application UI here.
    // For example, you might want to include a navigation bar, a sidebar, or other components.
    // This function can be used as the entry point for your Compose UI.

    Text("State is a ${state::class.simpleName}")

}