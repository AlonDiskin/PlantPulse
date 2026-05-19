package com.alon.plantpulse.usergarden.domain

/**
 * Represents the sunlight requirements for a plant.
 */
enum class PlantSunCare {
    /**
     * Requires direct sunlight for at least 6 hours a day.
     */
    FULL,

    /**
     * Requires a mix of direct sunlight and shade.
     */
    PARTIAL,

    /**
     * Thrives in indirect light or limited direct sunlight.
     */
    SHADE
}
