package com.vandenbreemen.neurons.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
    val mutationRate: Double = 0.1,
    val eliteSize: Int = 5,
    val learningRate: Double = 0.1,
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
        var mutationRate by remember { mutableStateOf(LastUsedParams.mutationRate.toString()) }
        var eliteSize by remember { mutableStateOf(LastUsedParams.eliteSize.toString()) }
        var learningRate by remember { mutableStateOf(LastUsedParams.learningRate.toString()) }
        var reuseGenePool by remember { mutableStateOf(false) }

        val canReuseGenePool = currentGenePool != null
        val isReusingGenePool = reuseGenePool && canReuseGenePool

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Genetic World Parameters") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
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
                    OutlinedTextField(
                        value = mutationRate,
                        onValueChange = { mutationRate = it },
                        label = { Text("Mutation Rate") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = eliteSize,
                        onValueChange = { eliteSize = it },
                        label = { Text("Number of Elite Genomes") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = learningRate,
                        onValueChange = { learningRate = it },
                        label = { Text("Learning Rate") },
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
                                mutationRate = mutationRate.toDouble(),
                                eliteSize = eliteSize.toInt(),
                                learningRate = learningRate.toDouble(),
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