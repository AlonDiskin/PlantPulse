package com.alon.plantpulse.usergarden.domain

/**
 * Represents the watering requirements for a plant.
 */
enum class PlantWaterCare {
    /**
     * Requires infrequent watering, drought tolerant.
     */
    LOW,

    /**
     * Requires regular watering when the soil starts to feel dry.
     */
    MODERATE,

    /**
     * Requires consistently moist soil, frequent watering.
     */
    HIGH
}
