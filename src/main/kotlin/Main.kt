import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.vandenbreemen.neurons.model.NeuralNet
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import com.vandenbreemen.neurons.model.Neuron

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Button(onClick = {
            text = "Simple Test Network Demo"
        }) {
            Text(text)
        }
    }
}


@Composable
fun NeuralNetworkDisplay(neuralNet: NeuralNet) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val cellWidth = size.width / neuralNet.cols
        val cellHeight = size.height / neuralNet.rows

        for (i in 0 until neuralNet.rows) {
            for (j in 0 until neuralNet.cols) {
                val neuron = neuralNet.getCellAt(i, j)
                val activation = neuron.activation
                val color = Color(activation.toFloat(), activation.toFloat(), activation.toFloat())
                drawRect(color, topLeft = Offset(j * cellWidth, i * cellHeight), size = Size(cellWidth, cellHeight))
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



        val points = (0 until width.toInt()).map { x ->
            val xValue = startPoint + x * xScale
            val yValue = f(xValue)
            Offset(x.toFloat(), ((1 - yValue) / yScale).toFloat())
        }

        drawPoints(points, pointMode = PointMode.Polygon, color = Color.Black)
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
    val neuralNet = NeuralNet(10, 10)

    neuralNet.getCellAt(5, 5).stimulate(-1.0)

    NeuralNetworkDisplay(neuralNet)
}

@Composable
@Preview
fun SigmoidCurveTester(){
    val neuron = Neuron()
    SigmoidFunctionPlotter(-5.0, 10.0, neuron::sigmoid)
}