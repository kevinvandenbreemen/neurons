package com.vandenbreemen.neurons.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vandenbreemen.neurons.evolution.GeneticPool

data class GeneticWorldParams(
    val brainSizeX: Int = 10,
    val brainSizeY: Int = 10,
    val numGenes: Int = 20,
    val numMovesPerTest: Int = 100,
    val costOfNotMoving: Double = 1.0,
    val reuseGenePool: Boolean = false
)

@Composable
fun GeneticWorldDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (GeneticWorldParams) -> Unit,
    currentGenePool: GeneticPool? = null
) {
    if (showDialog) {
        var brainSizeX by remember { mutableStateOf(LastUsedParams.brainSizeX.toString()) }
        var brainSizeY by remember { mutableStateOf(LastUsedParams.brainSizeY.toString()) }
        var numGenes by remember { mutableStateOf(LastUsedParams.numGenes.toString()) }
        var numMovesPerTest by remember { mutableStateOf(LastUsedParams.numMovesPerTest.toString()) }
        var costOfNotMoving by remember { mutableStateOf(LastUsedParams.costOfNotMoving.toString()) }
        var reuseGenePool by remember { mutableStateOf(false) }

        val canReuseGenePool = currentGenePool != null
        val isReusingGenePool = reuseGenePool && canReuseGenePool

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Genetic World Parameters") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (canReuseGenePool) {
                        Row {
                            Checkbox(
                                checked = reuseGenePool,
                                onCheckedChange = { reuseGenePool = it }
                            )
                            Text("Continue evolving existing gene pool")
                        }
                    }

                    OutlinedTextField(
                        value = brainSizeX,
                        onValueChange = { brainSizeX = it },
                        label = { Text("Brain Size X") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isReusingGenePool
                    )
                    OutlinedTextField(
                        value = brainSizeY,
                        onValueChange = { brainSizeY = it },
                        label = { Text("Brain Size Y") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isReusingGenePool
                    )
                    OutlinedTextField(
                        value = numGenes,
                        onValueChange = { numGenes = it },
                        label = { Text("Number of Genes") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isReusingGenePool
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
                                costOfNotMoving = costOfNotMoving.toDouble(),
                                reuseGenePool = reuseGenePool
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