import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.vandenbreemen.neurons.model.NeuralNet
import com.vandenbreemen.neurons.model.Neuron
import com.vandenbreemen.neurons.provider.GeneticNeuronProvider
import com.vandenbreemen.neurons.ui.NeuralNetworkDisplay
import com.vandenbreemen.neurons.ui.NeuronLegendDialog
import com.vandenbreemen.neurons.world.view.NeuralApplicationComposables
import com.vandenbreemen.neurons.world.viewmodel.NeuronApplicationViewModel
import kotlin.math.absoluteValue

@Composable
@Preview
fun App() {

    val applicationViewModel = NeuronApplicationViewModel()

    var showConnections by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showActivationColor by remember { mutableStateOf(true) }
    var showLegend by remember { mutableStateOf(false) }
    val dim = 25

    val neuralNet = NeuralNet(
        dim, dim,
        //RandomNeuronProvider(0.1, 0.01, fixedWeightNeuronPercentage = 0.01)

        GeneticNeuronProvider.generateGeneticProvider(dim, dim)
    )

    for (i in 0 until neuralNet.rows) {
        for (j in 0 until neuralNet.cols) {
            neuralNet.getCellAt(i, j).stimulate(((-5..5).random()).toDouble())
        }
    }
    neuralNet.applyAll()


    MaterialTheme {
        Column {
            Box {
                // View menu button and dropdown
                Button(onClick = { showMenu = true }) {
                    Text("View")
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(onClick = {
                        showConnections = true
                        showMenu = false
                    }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = showConnections,
                                onCheckedChange = { showConnections = it }
                            )
                            Text("Show Connections")
                        }
                    }
                    DropdownMenuItem(onClick = {
                        showActivationColor = !showActivationColor
                        showMenu = false
                    }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = showActivationColor,
                                onCheckedChange = { showActivationColor = it }
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
            
            NeuralNetworkDisplay(
                turnWait = 50L,
                neuralNet = neuralNet,
                showConnections = showConnections,
                showActivationColor = showActivationColor,
                onNeuronClick = { neuron ->
                    neuron.stimulate(10.0)
                }
            )
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
    Window(onCloseRequest = ::exitApplication) {
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

    NeuralNetworkDisplay(neuralNet)
}

@Composable
@Preview
fun SigmoidCurveTester(){
    val neuron = Neuron()
    SigmoidFunctionPlotter(-5.0, 5.0, neuron::sigmoid)
}