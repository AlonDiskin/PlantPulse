package com.alon.plantpulse.plantcare.domain

/**
 * Defines the watering intervals (in days) for different plant moisture needs.
 *
 * @property minDays The minimum number of days to wait between watering.
 * @property maxDays The maximum number of days to wait before the plant requires water.
 */
enum class PlantWateringFrequency(val minDays: Int, val maxDays: Int) {
    /** Low frequency watering, typical for succulents or cacti. */
    LOW(14, 21),
    /** Moderate frequency watering, typical for many common houseplants. */
    MODERATE(7, 14),
    /** High frequency watering, typical for moisture-loving plants or seedlings. */
    HIGH(3, 7)
}
