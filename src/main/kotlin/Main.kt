import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.vandenbreemen.neurons.evolution.model.GeneticWorldState
import com.vandenbreemen.neurons.evolution.view.GeneticWorldDialog
import com.vandenbreemen.neurons.model.NeuralNet
import com.vandenbreemen.neurons.model.Neuron
import com.vandenbreemen.neurons.ui.NeuralApplicationComposables
import com.vandenbreemen.neurons.ui.NeuralNetworkDisplay
import com.vandenbreemen.neurons.ui.NeuronLegendDialog
import com.vandenbreemen.neurons.world.viewmodel.NeuralNetworkDemoState
import com.vandenbreemen.neurons.world.viewmodel.NeuronApplicationViewModel
import com.vandenbreemen.neurons.world.viewmodel.StaticNeuralNetworkAppState
import kotlin.math.absoluteValue

@Composable
@Preview
fun App() {
    val applicationViewModel = NeuronApplicationViewModel()
    applicationViewModel.switchToApplication(NeuralNetworkDemoState(25))
    val coroutineScope = rememberCoroutineScope()

    var showMenu by remember { mutableStateOf(false) }
    var showLegend by remember { mutableStateOf(false) }
    var showApplicationsMenu by remember { mutableStateOf(false) }
    var showGeneticWorldDialog by remember { mutableStateOf(false) }

    MaterialTheme {
        Column {
            Box {
                Row {
                    // View menu button and dropdown
                    Button(onClick = { showMenu = true }) {
                        Text("View")
                    }

                    Spacer(Modifier.width(10.dp))

                    // Applications menu button and dropdown
                    Button(onClick = { showApplicationsMenu = true }) {
                        Text("Applications")
                    }
                    DropdownMenu(
                        expanded = showApplicationsMenu,
                        onDismissRequest = { showApplicationsMenu = false }
                    ) {
                        DropdownMenuItem(onClick = {
                            applicationViewModel.switchToApplication(NeuralNetworkDemoState(25))
                            showApplicationsMenu = false
                        }) {
                            Text("Neural network demo")
                        }
                        DropdownMenuItem(onClick = {
                            showGeneticWorldDialog = true
                            showApplicationsMenu = false
                        }) {
                            Text("Genetic World")
                        }
                    }
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(onClick = {
                        applicationViewModel.toggleShowConnections()
                        showMenu = false
                    }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = applicationViewModel.state.showConnections,
                                onCheckedChange = { applicationViewModel.toggleShowConnections() }
                            )
                            Text("Show Connections")
                        }
                    }
                    DropdownMenuItem(onClick = {
                        applicationViewModel.toggleActivationColor()
                        showMenu = false
                    }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = applicationViewModel.state.showActivationColor,
                                onCheckedChange = { applicationViewModel.toggleActivationColor() }
                            )
                            Text("Show Activation Color")
                        }
                    }
                    DropdownMenuItem(onClick = {
                        showLegend = true
                        showMenu = false
                    }) {
                        Text("Show Legend")
                    }
                }
            }

            // Show the legend dialog
            NeuronLegendDialog(
                showLegend = showLegend,
                onDismiss = { showLegend = false }
            )

            // Show the genetic world dialog
            GeneticWorldDialog(
                showDialog = showGeneticWorldDialog,
                onDismiss = { showGeneticWorldDialog = false },
                onConfirm = { params ->
                    val currentState = applicationViewModel.state
                    val currentGenePool = if (currentState is GeneticWorldState) {
                        currentState.getGenePool()
                    } else null

                    val state = GeneticWorldState(
                        minScore = params.minScore,
                        numWorlds = params.numWorldsToTest,
                        brainSizeX = params.brainSizeX,
                        brainSizeY = params.brainSizeY,
                        numGenes = params.numGenes,
                        numMovesPerTest = params.numMovesPerTest,
                        costOfNotMoving = params.costOfNotMoving,
                        mutationRate = params.mutationRate,
                        eliteSize = params.eliteSize,
                        worldWidth = params.worldWidth,
                        worldHeight = params.worldHeight,
                        wallDensity = params.wallDensity,
                        numEpochs = params.numEpochs,
                        numRooms = params.numRooms,
                        numRandomWalls = params.numRandomWalls,
                        errorWeight = params.errorWeight,
                        existingGenePool = if (params.reuseGenePool) currentGenePool else null
                    )
                    applicationViewModel.switchToApplication(state)
                    state.setup(coroutineScope)
                    showGeneticWorldDialog = false
                },
                currentGenePool = if (applicationViewModel.state is GeneticWorldState) {
                    (applicationViewModel.state as GeneticWorldState).getGenePool()
                } else null
            )

            Row {
                Column(modifier = Modifier.weight(0.5f)) {
                    when (val state = applicationViewModel.state) {
                        is NeuralNetworkDemoState -> NeuralNetworkDisplay(
                            turnWait = 50L,
                            demoState = state,
                            showConnections = state.showConnections,
                            showActivationColor = state.showActivationColor,
                            iterate = { applicationViewModel.iterate() },
                            onNeuronClick = { neuron ->
                                applicationViewModel.onSelectNeuron(neuron)
                            }
                        )
                        is GeneticWorldState -> {
                            if (state.isLoading) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {

                                    state.bestNeuralNetForDisplay?.let { bestNet ->
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text("Best Neural Network So Far:")
                                        NeuralNetworkDisplay(
                                            turnWait = 50L,
                                            demoState = StaticNeuralNetworkAppState(bestNet),
                                            showConnections = state.showConnections,
                                            showActivationColor = state.showActivationColor,
                                            iterate = { /* No iteration for best network display */ },
                                            onNeuronClick = { /* No neuron selection for best network display */ }
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Button(
                                            onClick = { state.useBestGenome() },
                                            enabled = state.hasFoundBestNeuralNet
                                        ) {
                                            Text("Use Best Genome")
                                        }
                                    }

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier.background(Color.Gray.copy(alpha = 0.5f))
                                    ) {
                                        CircularProgressIndicator()
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(state.setupProgress)
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text("Epoch ${state.currentEpoch} of ${state.totalEpochs}")
                                        Text("Best Score: ${state.bestScore}")

                                    }
                                }
                            } else {
                                NeuralNetworkDisplay(
                                    turnWait = 50L,
                                    demoState = state,
                                    showConnections = state.showConnections,
                                    showActivationColor = state.showActivationColor,
                                    iterate = { applicationViewModel.iterate() },
                                    onNeuronClick = { neuron ->
                                        applicationViewModel.onSelectNeuron(neuron)
                                    }
                                )
                            }
                        }
                    }
                }
                Column(modifier = Modifier.weight(0.5f)) {
                    NeuralApplicationComposables(applicationViewModel.state)
                }
            }
        }
    }
}



@Composable
fun SigmoidFunctionPlotter(
    startPoint: Double, endPoint: Double,
    f: (Double) -> Double) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        val xScale = (endPoint - startPoint) / width
        val yScale = 1.0 / height


        val zeroX = ((0 - startPoint) / xScale).toFloat()
        var closestToZero = Float.MAX_VALUE
        val points = (0 until width.toInt()).map { x ->
            val xValue = startPoint + x * xScale
            val yValue = f(xValue)
            Offset(x.toFloat(), ((1 - yValue) / yScale).toFloat()).also {
                if (yValue.absoluteValue < closestToZero) {
                    closestToZero = yValue.absoluteValue.toFloat()
                }
            }
        }
        closestToZero = (1 - closestToZero) / yScale.toFloat()

        drawLine(Color.Red, Offset(0f, closestToZero), Offset(width, closestToZero))

        drawPoints(points, pointMode = PointMode.Polygon, color = Color.Black)
        drawPoints(points, pointMode = PointMode.Polygon, color = Color.Black)
        drawLine(Color.Red, Offset(zeroX, 0f), Offset(zeroX, height))
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Neural Network Visualization",
        state = rememberWindowState(width = 1600.dp, height = 900.dp)
    ) {
        App()
    }
}


@Preview
@Composable
fun NeuralNetDisplayPreview() {

    val dim = 100

    val neuralNet = NeuralNet(dim, dim)

    for (i in 0 until neuralNet.rows) {
        for (j in 0 until neuralNet.cols) {
            neuralNet.getCellAt(i, j).stimulate(((-5..5).random()).toDouble())
        }
    }

    NeuralNetworkDisplay(
        NeuralNetworkDemoState(25),
        iterate = {})
}

@Composable
@Preview
fun SigmoidCurveTester(){
    val neuron = Neuron()
    SigmoidFunctionPlotter(-5.0, 5.0, neuron::sigmoid)
}