package com.alon.plantpulse.plantcare.ui.model

import com.alon.plantpulse.plantcare.application.model.PlantCareError

/**
 * UI state for the current light reading.
 *
 * @property luxValue The formatted lux value.
 * @property status The descriptive light status.
 * @property error The error encountered during light measurement, if any.
 */
data class LightMeterStatusUiState(
    val luxValue: String = "0",
    val status: String = "",
    val error: PlantCareError? = null
)

/**
 * UI state for the static plant information.
 *
 * @property plantName The common name of the plant.
 * @property recommendedRange The formatted recommended lux range.
 * @property error The error encountered while loading plant info, if any.
 */
data class PlantCareDiagnosticUiState(
    val plantName: String = "",
    val recommendedRange: String = "",
    val error: PlantCareError? = null
)
