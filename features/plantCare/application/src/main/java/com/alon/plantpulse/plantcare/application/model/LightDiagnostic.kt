package com.alon.plantpulse.plantcare.application.model

/**
 * Represents the status of light intensity for a specific plant.
 */
enum class LightStatus {
    /** Light intensity is below the plant's minimum requirement. */
    LOW,
    /** Light intensity is within the plant's optimal range. */
    SUFFICIENT,
    /** Light intensity is above the plant's maximum tolerance. */
    TOO_BRIGHT
}

/**
 * Diagnostic data containing the current light reading and its status.
 *
 * @property lux The raw light intensity value from the sensor.
 * @property status The calculated status based on plant requirements.
 */
data class LightDiagnostic(
    val lux: Float,
    val status: LightStatus
)
