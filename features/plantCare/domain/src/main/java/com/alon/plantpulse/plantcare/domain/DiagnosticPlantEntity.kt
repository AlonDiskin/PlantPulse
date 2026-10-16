package com.alon.plantpulse.plantcare.domain

/**
 * Domain entity representing a plant in the context of diagnostic care.
 *
 * This entity encapsulates the business logic for determining if environmental factors
 * (like light intensity) meet the specific needs of a plant.
 *
 * @property id The unique identifier of the plant.
 * @property commonName The display name of the plant.
 * @property lightRange The acceptable lux values for this plant.
 * @property wateringFrequency The recommended frequency for watering this plant.
 */
data class DiagnosticPlantEntity(
    val id: Int,
    val commonName: String,
    val lightRange: PlantLightRange,
    val wateringFrequency: PlantWateringFrequency
) {

    /**
     * Checks if the provided light intensity is below the minimum required for this plant.
     *
     * @param lux The current light intensity in lux.
     * @return True if the light is too low, false otherwise.
     */
    fun isLightLow(lux: Float): Boolean {
        return lightRange.minLux > lux
    }

    /**
     * Checks if the provided light intensity is above the maximum tolerated by this plant.
     *
     * @param lux The current light intensity in lux.
     * @return True if the light is too bright, false otherwise.
     */
    fun isLightHigh(lux: Float): Boolean {
        return lightRange.maxLux < lux
    }

    /**
     * Checks if the provided light intensity is within the optimal range for this plant.
     *
     * @param lux The current light intensity in lux.
     * @return True if the light level is sufficient, false otherwise.
     */
    fun isLightOk(lux: Float): Boolean {
        return lightRange.minLux <= lux && lightRange.maxLux >= lux
    }
}
