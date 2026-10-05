package com.alon.plantpulse.plantcare.application.usecase

import com.alon.plantpulse.common.application.Result
import com.alon.plantpulse.plantcare.api.PlantCareDataProvider
import com.alon.plantpulse.plantcare.api.PlantLight
import com.alon.plantpulse.plantcare.application.model.PlantCareDiagnosticDto
import com.alon.plantpulse.plantcare.application.model.PlantCareError
import com.alon.plantpulse.plantcare.domain.PlantLightRange
import javax.inject.Inject

/**
 * Use case to retrieve diagnostic care requirements for a specific plant.
 *
 * This use case provides the static reference data (like common name and ideal light range)
 * needed to perform and display a light sufficiency diagnosis.
 *
 * @property plantCareDataProvider Provider for the care requirements of the plant.
 */
class GetPlantCareDiagnosticUseCase @Inject constructor(
    private val plantCareDataProvider: PlantCareDataProvider
) {

    /**
     * Retrieves diagnostic data for the plant identified by [plantId].
     *
     * @param plantId The unique identifier of the plant.
     * @return A [Result] containing [PlantCareDiagnosticDto] on success, 
     * or [PlantCareError] on failure.
     */
    suspend operator fun invoke(plantId: Int): Result<PlantCareDiagnosticDto, PlantCareError> {
        return try {
            val data = plantCareDataProvider.providePlantCareData(plantId)
            
            val recommendedRange = when (data.light) {
                PlantLight.FULL -> PlantLightRange.FULL
                PlantLight.PARTIAL -> PlantLightRange.PARTIAL
                PlantLight.SHADE -> PlantLightRange.SHADE
            }

            Result.Success(
                PlantCareDiagnosticDto(
                    commonName = data.commonName,
                    recommendedLightRange = recommendedRange
                )
            )
        } catch (e: Exception) {
            Result.Failure(PlantCareError.Internal(e))
        }
    }
}
