package com.vandenbreemen.neurons.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class GeneticWorldParams(
    val brainSizeX: Int = 10,
    val brainSizeY: Int = 10,
    val numGenes: Int = 20,
    val numMovesPerTest: Int = 100,
    val costOfNotMoving: Double = 0.1
)

@Composable
fun GeneticWorldDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (GeneticWorldParams) -> Unit
) {
    if (showDialog) {
        var brainSizeX by remember { mutableStateOf(LastUsedParams.brainSizeX.toString()) }
        var brainSizeY by remember { mutableStateOf(LastUsedParams.brainSizeY.toString()) }
        var numGenes by remember { mutableStateOf(LastUsedParams.numGenes.toString()) }
        var numMovesPerTest by remember { mutableStateOf(LastUsedParams.numMovesPerTest.toString()) }
        var costOfNotMoving by remember { mutableStateOf(LastUsedParams.costOfNotMoving.toString()) }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Genetic World Parameters") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = brainSizeX,
                        onValueChange = { brainSizeX = it },
                        label = { Text("Brain Size X") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = brainSizeY,
                        onValueChange = { brainSizeY = it },
                        label = { Text("Brain Size Y") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = numGenes,
                        onValueChange = { numGenes = it },
                        label = { Text("Number of Genes") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = numMovesPerTest,
                        onValueChange = { numMovesPerTest = it },
                        label = { Text("Moves per Test") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = costOfNotMoving,
                        onValueChange = { costOfNotMoving = it },
                        label = { Text("Cost of Not Moving") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        try {
                            val params = GeneticWorldParams(
                                brainSizeX = brainSizeX.toInt(),
                                brainSizeY = brainSizeY.toInt(),
                                numGenes = numGenes.toInt(),
                                numMovesPerTest = numMovesPerTest.toInt(),
                                costOfNotMoving = costOfNotMoving.toDouble()
                            )
                            // Update last used params
                            LastUsedParams = params
                            onConfirm(params)
                        } catch (e: NumberFormatException) {
                            // Handle invalid input
                        }
                    }
                ) {
                    Text("Start")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}

// Object to store the last used parameters
private var LastUsedParams = GeneticWorldParams() 