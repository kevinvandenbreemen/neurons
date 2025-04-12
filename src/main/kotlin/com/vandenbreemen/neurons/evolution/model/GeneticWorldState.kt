package com.vandenbreemen.neurons.evolution.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.vandenbreemen.neurons.agent.NeuralAgent
import com.vandenbreemen.neurons.evolution.GeneticPool
import com.vandenbreemen.neurons.model.NeuralNet
import com.vandenbreemen.neurons.world.controller.NavigationWorldSimulation
import com.vandenbreemen.neurons.world.driver.GeneticWorldDriver
import com.vandenbreemen.neurons.world.viewmodel.NeuralNetApplicationState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GeneticWorldState(
    numWorlds: Int = 5,
    private val brainSizeX: Int = 10,
    private val brainSizeY: Int = 10,
    numGenes: Int = 20,
    numMovesPerTest: Int = 100,
    costOfNotMoving: Double = 0.1,
    mutationRate: Double = 0.1,
    pruningRate: Double = 0.05,
    eliteSize: Int = 5,
    private val learningRate: Double = 0.1,
    worldWidth: Int = 100,
    worldHeight: Int = 100,
    wallDensity: Double = 0.001,
    numEpochs: Int = 10,
    numRooms: Int = 2,
    numRandomWalls: Int = 2,
    private val numWorldsToTest: Int = 3,
    private val newGeneProbability: Double = 0.1,
    private val painTolerance: Double = 0.5,
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
        pruningRate = pruningRate,
        eliteSize = eliteSize,
        learningRate = learningRate,
        worldWidth = worldWidth,
        worldHeight = worldHeight,
        wallDensity = wallDensity,
        numEpochs = numEpochs,
        numRooms = numRooms,
        numRandomWalls = numRandomWalls,
        existingGenePool = existingGenePool,
        newGeneProbability = newGeneProbability,
    )

    private var navigationSimulation: NavigationWorldSimulation? = null
    var navSimulationForDisplay by mutableStateOf<NavigationWorldSimulation?>(null)
    var isLoading by mutableStateOf(false)
    var setupProgress by mutableStateOf("")
    var currentEpoch by mutableStateOf(0)
    var totalEpochs by mutableStateOf(numEpochs)
    var bestScore by mutableStateOf(0.0)
    private var bestNeuralNet: NeuralNet? = null
    val hasFoundBestNeuralNet: Boolean
        get() = bestNeuralNet != null

    var bestNeuralNetForDisplay by mutableStateOf<NeuralNet?>(null)
    var isUsingBestGenome by mutableStateOf(false)
    private var setupJob: kotlinx.coroutines.Job? = null

    override var neuralNet by mutableStateOf<NeuralNet?>(null)

    fun getGenePool(): GeneticPool? {
        return driver.getGenePool()
    }

    fun useBestGenome() {
        if (bestNeuralNet != null) {
            // Cancel any ongoing setup
            setupJob?.cancel()
            setupJob = null

            isUsingBestGenome = true
            neuralNet = bestNeuralNet
            // Create a new simulation with the best neural network
            val world = driver.getRandomWorld()
            val agent = NeuralAgent(bestNeuralNet!!, learningRate)
            navigationSimulation = NavigationWorldSimulation(world).apply {
                addAgent(agent, world.getRandomEmptyCell())
            }
            navSimulationForDisplay = navigationSimulation
            isLoading = false
            setupProgress = ""
        }
    }

    fun setup(coroutineScope: CoroutineScope) {
        // Cancel any existing setup job
        setupJob?.cancel()

        isLoading = true
        setupProgress = "Initializing genetic algorithm..."
        currentEpoch = 0
        bestScore = 0.0
        bestNeuralNet = null
        bestNeuralNetForDisplay = null
        isUsingBestGenome = false

        setupJob = coroutineScope.launch {
            try {
                withContext(Dispatchers.Default) {
                    setupProgress = if (existingGenePool != null) {
                        "Continuing evolution of existing gene pool..."
                    } else {
                        "Running genetic algorithm..."
                    }
                    driver.drive(
                        fitnessFunction = { geneticNeuronProvider, numMoves ->
                            val score =
                                driver.getFitness(geneticNeuronProvider, numMoves, numWorldsToTest, painTolerance)
                            if (score > bestScore) {
                                bestScore = score
                                // Create and store the best neural network
                                bestNeuralNet = NeuralNet(
                                    this@GeneticWorldState.brainSizeX,
                                    this@GeneticWorldState.brainSizeY,
                                    geneticNeuronProvider
                                )
                                bestNeuralNetForDisplay = bestNeuralNet
                            }
                            score
                        },
                        onEpochComplete = { epoch, score ->
                            currentEpoch = epoch
                            if (score > bestScore) {
                                bestScore = score
                            }
                        }
                    )

                    if (!isUsingBestGenome) {
                        setupProgress = "Creating neural network..."
                        neuralNet = driver.getRandomNeuralNetwork()

                        setupProgress = "Setting up navigation simulation..."
                        val pairForDisplay = driver.createSimulationWithAgent()
                        navigationSimulation = pairForDisplay.first
                        navSimulationForDisplay = navigationSimulation
                        neuralNet = pairForDisplay.second
                    }
                }
            } finally {
                if (!isUsingBestGenome) {
                    isLoading = false
                    setupProgress = ""
                }
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