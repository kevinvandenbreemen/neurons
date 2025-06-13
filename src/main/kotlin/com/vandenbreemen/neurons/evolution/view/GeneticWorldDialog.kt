package com.vandenbreemen.neurons.evolution.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vandenbreemen.neurons.evolution.model.GeneticPool

data class GeneticWorldParams(
    val brainSizeX: Int = 10,
    val brainSizeY: Int = 10,
    val numGenes: Int = 20,
    val numMovesPerTest: Int = 100,
    val costOfNotMoving: Double = 1.0,
    val mutationRate: Double = 0.1,
    val eliteSize: Int = 5,
    val learningRate: Double = 0.1,
    val worldWidth: Int = 100,
    val worldHeight: Int = 100,
    val wallDensity: Double = 0.001,
    val numEpochs: Int = 10,
    val numRooms: Int = 2,
    val numRandomWalls: Int = 2,
    val numWorldsToTest: Int = 3,
    val newGeneProbability: Double = 0.1,
    val errorWeight: Double = 1.0,
    val reuseGenePool: Boolean = false
)

@Composable
fun GeneticWorldDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (GeneticWorldParams) -> Unit,
    currentGenePool: GeneticPool? = null
) {
    var brainSizeX by remember { mutableStateOf("10") }
    var brainSizeY by remember { mutableStateOf("10") }
    var numGenes by remember { mutableStateOf("100") }
    var numMovesPerTest by remember { mutableStateOf("400") }
    var costOfNotMoving by remember { mutableStateOf("10.0") }
    var mutationRate by remember { mutableStateOf("0.2") }
    var eliteSize by remember { mutableStateOf("20") }
    var worldWidth by remember { mutableStateOf("100") }
    var worldHeight by remember { mutableStateOf("100") }
    var wallDensity by remember { mutableStateOf("0.001") }
    var numEpochs by remember { mutableStateOf("1000") }
    var numRooms by remember { mutableStateOf("5") }
    var numRandomWalls by remember { mutableStateOf("2") }
    var numWorldsToTest by remember { mutableStateOf("10") }
    var newGeneProbability by remember { mutableStateOf("0.2") }
    var errorWeight by remember { mutableStateOf("45.0") }
    var isReusingGenePool by remember { mutableStateOf(currentGenePool != null) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Genetic World Parameters") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Column(
                            modifier = Modifier.weight(1f).padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Organism Parameers", style = MaterialTheme.typography.subtitle1)
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
                                value = mutationRate,
                                onValueChange = { mutationRate = it },
                                label = { Text("Mutation Rate") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = eliteSize,
                                onValueChange = { eliteSize = it },
                                label = { Text("Elite Size") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Left column - Genetic Algorithm Parameters
                        Column(
                            modifier = Modifier.weight(1f).padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("World Parameters", style = MaterialTheme.typography.subtitle1)
                            OutlinedTextField(
                                value = numWorldsToTest,
                                onValueChange = { numWorldsToTest = it },
                                label = { Text("Number of Worlds to Test") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = worldWidth,
                                onValueChange = { worldWidth = it },
                                label = { Text("World Width") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = worldHeight,
                                onValueChange = { worldHeight = it },
                                label = { Text("World Height") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = wallDensity,
                                onValueChange = { wallDensity = it },
                                label = { Text("Wall Density") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = numRooms,
                                onValueChange = { numRooms = it },
                                label = { Text("Number of Rooms") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = numRandomWalls,
                                onValueChange = { numRandomWalls = it },
                                label = { Text("Number of Random Walls") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = numMovesPerTest,
                                onValueChange = { numMovesPerTest = it },
                                label = { Text("Number of Moves per Test") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Right column - World Parameters
                        Column(
                            modifier = Modifier.weight(1f).padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            Text("Genetic Algorithm Parameters", style = MaterialTheme.typography.subtitle1)


                            OutlinedTextField(
                                value = numEpochs,
                                onValueChange = { numEpochs = it },
                                label = { Text("Number of Epochs") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = newGeneProbability,
                                onValueChange = { newGeneProbability = it },
                                label = { Text("New Gene Probability") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = costOfNotMoving,
                                onValueChange = { costOfNotMoving = it },
                                label = { Text("Cost of Not Moving") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = errorWeight,
                                onValueChange = { errorWeight = it },
                                label = { Text("Error Weight") },
                                modifier = Modifier.fillMaxWidth()
                            )


                        }


                    }

                    if (currentGenePool != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isReusingGenePool,
                                onCheckedChange = { isReusingGenePool = it }
                            )
                            Text("Reuse existing gene pool")
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirm(
                            GeneticWorldParams(
                                brainSizeX = brainSizeX.toIntOrNull() ?: 10,
                                brainSizeY = brainSizeY.toIntOrNull() ?: 10,
                                numGenes = numGenes.toIntOrNull() ?: 20,
                                numMovesPerTest = numMovesPerTest.toIntOrNull() ?: 100,
                                costOfNotMoving = costOfNotMoving.toDoubleOrNull() ?: 1.0,
                                mutationRate = mutationRate.toDoubleOrNull() ?: 0.1,
                                eliteSize = eliteSize.toIntOrNull() ?: 5,
                                learningRate = 0.1,
                                worldWidth = worldWidth.toIntOrNull() ?: 100,
                                worldHeight = worldHeight.toIntOrNull() ?: 100,
                                wallDensity = wallDensity.toDoubleOrNull() ?: 0.001,
                                numEpochs = numEpochs.toIntOrNull() ?: 10,
                                numRooms = numRooms.toIntOrNull() ?: 2,
                                numRandomWalls = numRandomWalls.toIntOrNull() ?: 2,
                                numWorldsToTest = numWorldsToTest.toIntOrNull() ?: 3,
                                newGeneProbability = newGeneProbability.toDoubleOrNull() ?: 0.1,
                                errorWeight = errorWeight.toDoubleOrNull() ?: 1.0,
                                reuseGenePool = isReusingGenePool
                            )
                        )
                    }
                ) {
                    Text("Start")
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}

// Object to store the last used parameters
private var LastUsedParams = GeneticWorldParams()