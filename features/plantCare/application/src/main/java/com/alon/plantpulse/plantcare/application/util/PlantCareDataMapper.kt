package com.alon.plantpulse.plantcare.application.util

import com.alon.plantpulse.plantcare.api.PlantCareData
import com.alon.plantpulse.plantcare.api.PlantLight
import com.alon.plantpulse.plantcare.api.PlantWatering
import com.alon.plantpulse.plantcare.domain.DiagnosticPlantEntity
import com.alon.plantpulse.plantcare.domain.PlantLightRange
import com.alon.plantpulse.plantcare.domain.PlantWateringFrequency
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mapper responsible for converting API data models into domain entities for plant care.
 */
@Singleton
class PlantCareDataMapper @Inject constructor() {

    /**
     * Maps a [PlantCareData] DTO to a [DiagnosticPlantEntity] domain model.
     *
     * @param data The plant care data received from the API/Data layer.
     * @return A domain entity containing business-ready care requirements.
     */
    fun mapToEntity(data: PlantCareData): DiagnosticPlantEntity {
        return DiagnosticPlantEntity(
            id = data.id,
            commonName = data.commonName,
            lightRange = mapLightRange(data.light),
            wateringFrequency = mapWateringFrequency(data.watering)
        )
    }

    private fun mapLightRange(light: PlantLight): PlantLightRange {
        return when (light) {
            PlantLight.FULL -> PlantLightRange.FULL
            PlantLight.PARTIAL -> PlantLightRange.PARTIAL
            PlantLight.SHADE -> PlantLightRange.SHADE
        }
    }

    private fun mapWateringFrequency(watering: PlantWatering): PlantWateringFrequency {
        return when (watering) {
            PlantWatering.LOW -> PlantWateringFrequency.LOW
            PlantWatering.MODERATE -> PlantWateringFrequency.MODERATE
            PlantWatering.HIGH -> PlantWateringFrequency.HIGH
        }
    }
}
