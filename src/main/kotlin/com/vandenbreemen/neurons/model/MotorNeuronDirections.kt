package com.vandenbreemen.neurons.model

/**
 * Constants for motor neuron direction byte ranges.
 * Each direction is allocated 32 bytes (0x20) of the available 256 bytes (0x00-0xFF).
 */
object MotorNeuronDirections {
    // North: 0x00-0x1F
    const val NORTH_START: Byte = 0x00.toByte()
    const val NORTH_END: Byte = 0x1F.toByte()

    // Northeast: 0x20-0x3F
    const val NORTHEAST_START: Byte = 0x20.toByte()
    const val NORTHEAST_END: Byte = 0x3F.toByte()

    // East: 0x40-0x5F
    const val EAST_START: Byte = 0x40.toByte()
    const val EAST_END: Byte = 0x5F.toByte()

    // Southeast: 0x60-0x7F
    const val SOUTHEAST_START: Byte = 0x60.toByte()
    const val SOUTHEAST_END: Byte = 0x7F.toByte()

    // South: 0x80-0x9F
    const val SOUTH_START: Byte = 0x80.toByte()
    const val SOUTH_END: Byte = 0x9F.toByte()

    // Southwest: 0xA0-0xBF
    const val SOUTHWEST_START: Byte = 0xA0.toByte()
    const val SOUTHWEST_END: Byte = 0xBF.toByte()

    // West: 0xC0-0xDF
    const val WEST_START: Byte = 0xC0.toByte()
    const val WEST_END: Byte = 0xDF.toByte()

    // Northwest: 0xE0-0xFF
    const val NORTHWEST_START: Byte = 0xE0.toByte()
    const val NORTHWEST_END: Byte = 0xFF.toByte()
} 