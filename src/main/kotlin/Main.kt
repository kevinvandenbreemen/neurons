import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.vandenbreemen.neurons.model.*
import com.vandenbreemen.neurons.provider.RandomNeuronProvider
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

@Composable
@Preview
fun App() {
    var showConnections by remember { mutableStateOf(true) }
    var showMenu by remember { mutableStateOf(false) }
    val dim = 10

    val neuralNet = NeuralNet(
        dim, dim,
        RandomNeuronProvider(0.1, 0.01, fixedWeightNeuronPercentage = 0.01)

        //GeneticNeuronProvider.generateGeneticProvider(dim, dim)
    )

    for (i in 0 until neuralNet.rows) {
        for (j in 0 until neuralNet.cols) {
            neuralNet.getCellAt(i, j).stimulate(((-5..5).random()).toDouble())
        }
    }
    neuralNet.applyAll()


    MaterialTheme {
        Column {
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
                    Text("Show Connections")
                }
                DropdownMenuItem(onClick = {
                    showConnections = false
                    showMenu = false
                }) {
                    Text("Hide Connections")
                }
            }
            NeuralNetworkDisplay(
                turnWait = 50L,
                neuralNet = neuralNet,
                showConnections = showConnections,
                onNeuronClick = { neuron ->
//                println("Clicked neuron with activation: ${neuron.activation}")
//                println("Number of connections: ${neuron.connections.size}")
//                neuron.connections.forEach { connection ->
//
//                }

                    neuron.stimulate(10.0)

                }
            )
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NeuralNetworkDisplay(
    neuralNet: NeuralNet,
    turnWait: Long = 100,
    showConnections: Boolean = true,
    onNeuronClick: ((Neuron) -> Unit)? = null
) {
    var fireCount by remember { mutableStateOf(0) }
    var pointerInput by remember { mutableStateOf<PointerInputChange?>(null) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(turnWait)
            neuralNet.fireAndUpdate()
            neuralNet.updateAllWeights(0.001)  // Update weights after firing
            fireCount++ // Trigger recomposition
        }
    }

    Column {
        Text("Turns: $fireCount")
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .onPointerEvent(PointerEventType.Press) { event ->
                    pointerInput = event.changes.firstOrNull()
                }
        ) {
            val cellWidth = size.width / neuralNet.cols
            val cellHeight = size.height / neuralNet.rows

            // Handle click if we have a pointer input
            val input = pointerInput
            if (input != null) {
                val x = input.position.x
                val y = input.position.y
                val row = (y / cellHeight).toInt()
                val col = (x / cellWidth).toInt()
                if (row in 0 until neuralNet.rows && col in 0 until neuralNet.cols) {
                    println("Clicked on neuron at $row, $col")
                    onNeuronClick?.invoke(neuralNet.getCellAt(row, col))
                }
                pointerInput = null
            }

            for (i in 0 until neuralNet.rows) {
                for (j in 0 until neuralNet.cols) {
                    val neuron = neuralNet.getCellAt(i, j)
                    val activation = neuron.activation
                    val color = Color(activation.toFloat(), activation.toFloat(), activation.toFloat())
                    drawRect(color, topLeft = Offset(j * cellWidth, i * cellHeight), size = Size(cellWidth, cellHeight))

                    // Draw indicators for special neuron types
                    val centerX = j * cellWidth + cellWidth / 2
                    val centerY = i * cellHeight + cellHeight / 2
                    val dotRadius = minOf(cellWidth, cellHeight) * 0.15f

                    when (neuron) {
                        is InhibitoryNeuron -> {
                            drawCircle(
                                color = Color.Red,
                                radius = dotRadius,
                                center = Offset(centerX, centerY)
                            )
                        }

                        is SineNeuron -> {
                            drawCircle(
                                color = Color.Green,
                                radius = dotRadius,
                                center = Offset(centerX, centerY)
                            )
                        }

                        is FixedWeightNeuron -> {
                            drawCircle(
                                color = Color.Blue,
                                radius = dotRadius,
                                center = Offset(centerX, centerY)
                            )
                        }

                        else -> {} // Regular neuron, no indicator needed
                    }

                    if (showConnections) {
                        Direction.entries.forEach { direction ->
                            val strength = neuralNet.getConnectionStrengthFrom(i, j, direction)
                            if (strength != 0.0) {
                                val (dx, dy) = when (direction) {
                                    Direction.UP -> 0 to -1
                                    Direction.DOWN -> 0 to 1
                                    Direction.LEFT -> -1 to 0
                                    Direction.RIGHT -> 1 to 0
                                    Direction.UP_LEFT -> -1 to -1
                                    Direction.UP_RIGHT -> 1 to -1
                                    Direction.DOWN_LEFT -> -1 to 1
                                    Direction.DOWN_RIGHT -> 1 to 1
                                }
                                val startX = j * cellWidth + cellWidth / 2
                                val startY = i * cellHeight + cellHeight / 2
                                val endX = (j + dx) * cellWidth + cellWidth / 2
                                val endY = (i + dy) * cellHeight + cellHeight / 2

                                // Calculate the offset from center for start and end points
                                val offset = minOf(cellWidth, cellHeight) * 0.2f // 20% of cell size
                                val startOffsetX = startX + dx * offset
                                val startOffsetY = startY + dy * offset
                                val endOffsetX = endX - dx * offset
                                val endOffsetY = endY - dy * offset

                                // Calculate the middle point with a perpendicular offset
                                val midX = (startOffsetX + endOffsetX) / 2
                                val midY = (startOffsetY + endOffsetY) / 2
                                val perpOffset = minOf(cellWidth, cellHeight) * 0.3f // 30% of cell size
                                val perpX = midX + dy * perpOffset
                                val perpY = midY - dx * perpOffset

                                // Draw three connected lines
                                val connectionColor = if (strength < 0)
                                    Color.Red.copy(alpha = strength.absoluteValue.toFloat().coerceIn(0f, 1f))
                                else
                                    Color.Green.copy(alpha = strength.toFloat().coerceIn(0f, 1f))

                                // First line from start to middle point
                                drawLine(
                                    color = connectionColor,
                                    start = Offset(startOffsetX, startOffsetY),
                                    end = Offset(perpX, perpY),
                                    strokeWidth = 1f
                                )

                                // Second line from middle point to end
                                drawLine(
                                    color = connectionColor,
                                    start = Offset(perpX, perpY),
                                    end = Offset(endOffsetX, endOffsetY),
                                    strokeWidth = 1f
                                )

                                // Draw arrow at the end
                                val arrowSize = minOf(cellWidth, cellHeight) * 0.15f // Size of the arrow
                                val angle = kotlin.math.atan2(endOffsetY - perpY, endOffsetX - perpX)
                                val arrowAngle1 = angle + Math.PI / 6 // 30 degrees
                                val arrowAngle2 = angle - Math.PI / 6 // -30 degrees

                                // Calculate arrow points
                                val arrowPoint1 = Offset(
                                    (endOffsetX - arrowSize * kotlin.math.cos(arrowAngle1)).toFloat(),
                                    (endOffsetY - arrowSize * kotlin.math.sin(arrowAngle1)).toFloat()
                                )
                                val arrowPoint2 = Offset(
                                    (endOffsetX - arrowSize * kotlin.math.cos(arrowAngle2)).toFloat(),
                                    (endOffsetY - arrowSize * kotlin.math.sin(arrowAngle2)).toFloat()
                                )

                                // Draw arrow lines
                                drawLine(
                                    color = connectionColor,
                                    start = Offset(endOffsetX, endOffsetY),
                                    end = arrowPoint1,
                                    strokeWidth = 1f
                                )
                                drawLine(
                                    color = connectionColor,
                                    start = Offset(endOffsetX, endOffsetY),
                                    end = arrowPoint2,
                                    strokeWidth = 1f
                                )
                            }
                        }
                    }
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