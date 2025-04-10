package com.vandenbreemen.neurons.world.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.vandenbreemen.neurons.evolution.GeneticPool
import com.vandenbreemen.neurons.model.NeuralNet
import com.vandenbreemen.neurons.model.Neuron
import com.vandenbreemen.neurons.provider.GeneticNeuronProvider
import com.vandenbreemen.neurons.world.controller.NavigationWorldSimulation
import com.vandenbreemen.neurons.world.driver.GeneticWorldDriver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class NeuralNetApplicationState {
    var currentTurn by mutableStateOf(0)
    var selectedNeuron by mutableStateOf<NeuronInfoState?>(null)
    var showConnections by mutableStateOf(false)
    var showActivationColor by mutableStateOf(true)

    open val neuralNet: NeuralNet? = null

    protected open fun doIterate() {

    }

    fun iterate(): NeuralNetApplicationState {
        currentTurn++
        doIterate()
        selectedNeuron?.let {
            selectedNeuron = it.copy() // Copy the state to trigger recomposition
        }
        return this
    }

    fun selectNeuron(neuron: Neuron) {
        selectedNeuron = NeuronInfoState(neuron)
    }
}

class NeuralNetworkDemoState(dim: Int) : NeuralNetApplicationState() {

    override val neuralNet by mutableStateOf<NeuralNet>(
        NeuralNet(
            dim, dim,

            GeneticNeuronProvider.generateGeneticProvider(dim, dim)
        ).also { neuralNet ->
            for (i in 0 until neuralNet.rows) {
                for (j in 0 until neuralNet.cols) {
                    neuralNet.getCellAt(i, j).stimulate(((-5..5).random()).toDouble())
                }
            }
            neuralNet.applyAll()
        })

    override fun doIterate() {
        neuralNet.fireAndUpdate()
        neuralNet.updateAllWeights(0.001)  // Update weights after firing
    }

}

class GeneticWorldState(
    numWorlds: Int = 5,
    brainSizeX: Int = 10,
    brainSizeY: Int = 10,
    numGenes: Int = 20,
    numMovesPerTest: Int = 100,
    costOfNotMoving: Double = 0.1,
    mutationRate: Double = 0.1,
    eliteSize: Int = 5,
    learningRate: Double = 0.1,
    worldWidth: Int = 100,
    worldHeight: Int = 100,
    wallDensity: Double = 0.001,
    numEpochs: Int = 10,
    private val existingGenePool: GeneticPool? = null
) : NeuralNetApplicationState() {
    private val driver = GeneticWorldDriver(
        numWorlds = numWorlds,
        brainSizeX = brainSizeX,
        brainSizeY = brainSizeY,
        numGenes = numGenes,
        numMovesPerTest = numMovesPerTest,
        costOfNotMoving = costOfNotMoving,
        mutationRate = mutationRate,
        eliteSize = eliteSize,
        learningRate = learningRate,
        worldWidth = worldWidth,
        worldHeight = worldHeight,
        wallDensity = wallDensity,
        numEpochs = numEpochs,
        existingGenePool = existingGenePool
    )

    private var navigationSimulation: NavigationWorldSimulation? = null
    var navSimulationForDisplay by mutableStateOf<NavigationWorldSimulation?>(null)
    var isLoading by mutableStateOf(false)
    var setupProgress by mutableStateOf("")
    var currentEpoch by mutableStateOf(0)
    var totalEpochs by mutableStateOf(numEpochs)

    override var neuralNet by mutableStateOf<NeuralNet?>(null)

    fun getGenePool(): GeneticPool? {
        return driver.getGenePool()
    }

    fun setup(coroutineScope: CoroutineScope) {
        isLoading = true
        setupProgress = "Initializing genetic algorithm..."
        currentEpoch = 0

        coroutineScope.launch {
            try {
                withContext(Dispatchers.Default) {
                    setupProgress = if (existingGenePool != null) {
                        "Continuing evolution of existing gene pool..."
                    } else {
                        "Running genetic algorithm..."
                    }
                    driver.drive(
                        fitnessFunction = driver::getFitness,
                        onEpochComplete = { epoch ->
                            currentEpoch = epoch
                        }
                    )

                    setupProgress = "Creating neural network..."
                    neuralNet = driver.getRandomNeuralNetwork()

                    setupProgress = "Setting up navigation simulation..."
                    val pairForDisplay = driver.createSimulationWithAgent()
                    navigationSimulation = pairForDisplay.first
                    navSimulationForDisplay = navigationSimulation
                    neuralNet = pairForDisplay.second
                }
            } finally {
                isLoading = false
                setupProgress = ""
            }
        }
    }

    override fun doIterate() {

        if (navigationSimulation == null) {
            return
        }

        neuralNet?.fireAndUpdate()
        neuralNet?.updateAllWeights(0.001)  // Update weights after firing
        navigationSimulation?.step()
        navSimulationForDisplay = navigationSimulation
    }
}