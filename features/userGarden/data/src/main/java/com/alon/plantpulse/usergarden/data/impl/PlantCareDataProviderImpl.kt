package com.alon.plantpulse.usergarden.data.impl

import com.alon.plantpulse.plantcare.api.PlantCareData
import com.alon.plantpulse.plantcare.api.PlantCareDataProvider
import com.alon.plantpulse.plantcare.api.PlantLight
import com.alon.plantpulse.usergarden.data.local.PlantDao
import com.alon.plantpulse.usergarden.domain.PlantSunCare
import javax.inject.Inject

class PlantCareDataProviderImpl @Inject constructor(private val plantDao: PlantDao) : PlantCareDataProvider {

    override suspend fun providePlantCareData(id: Int): PlantCareData {
        try {
            val plant = plantDao.getById(id)
            val plantLight = when(plant.sunCare) {
               PlantSunCare.PARTIAL -> PlantLight.PARTIAL
               PlantSunCare.FULL -> PlantLight.FULL
               PlantSunCare.SHADE -> PlantLight.SHADE
            }
            return PlantCareData(plant.id, plant.commonName, plantLight)
        } catch (e: Exception) {
            throw e
        }
    }
}