package com.vandenbreemen.neurons.math

import kotlin.math.abs
import kotlin.math.exp

fun sigmoid(z: Double): Double {
    return 1.0 / (1.0 + exp(-z))
}

/**
 * A sigmoid-like function that increases when two input values are close and high.
 *
 * @param x The first input value.
 * @param y The second input value.
 * @param m Scaling factor related to the expected range of input values.
 * @param a Scaling factor for the proximity term.
 * @param b Scaling factor for the magnitude term.
 * @param c Bias term.
 * @return A value between 0 and 1.
 */
fun combinedSigmoidV1(x: Double, y: Double, m: Double, a: Double, b: Double, c: Double): Double {
    val proximity = m - abs(x - y)
    val magnitude = (x + y) / 2.0
    val z = a * proximity + b * magnitude - c
    return sigmoid(z)
}

/**
 * Sigmoid function that starts at roughly 0 when x is 0 and approaches 1 as x increases to 1.0.
 */
//  Tweaked this using https://www.desmos.com/calculator/coknirwubg
fun sigmoidFromZero(x: Double): Double {
    return 1 / (1 + exp(-(6.5 * x - 3.5)))
}