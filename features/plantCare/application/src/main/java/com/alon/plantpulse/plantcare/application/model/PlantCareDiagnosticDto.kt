package com.alon.plantpulse.plantcare.application.model

import com.alon.plantpulse.plantcare.domain.PlantLightRange

/**
 * Data representation for plant care diagnostic information.
 *
 * @property commonName The common name of the plant.
 * @property recommendedLightRange The optimal light intensity range for this plant.
 */
data class PlantCareDiagnosticDto(
    val commonName: String,
    val recommendedLightRange: PlantLightRange
)
