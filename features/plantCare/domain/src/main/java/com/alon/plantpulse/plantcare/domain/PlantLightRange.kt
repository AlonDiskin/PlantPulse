package com.alon.plantpulse.plantcare.domain

/**
 * Defines the acceptable light intensity ranges (in Lux) for different plant categories.
 *
 * @property minLux The minimum light intensity required for healthy growth.
 * @property maxLux The maximum light intensity the plant can tolerate before risk of damage.
 */
enum class PlantLightRange(val minLux: Float, val maxLux: Float) {
    /** Optimal for plants requiring direct sunlight. */
    FULL(5000f, 10000f),
    /** Optimal for plants requiring indirect or partial sunlight. */
    PARTIAL(1500f, 5000f),
    /** Optimal for plants that thrive in low-light conditions. */
    SHADE(500f, 1500f)
}
