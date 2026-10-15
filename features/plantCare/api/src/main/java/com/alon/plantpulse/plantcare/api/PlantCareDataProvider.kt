package com.alon.plantpulse.plantcare.api

interface PlantCareDataProvider {

    suspend fun providePlantCareData(id: Int): PlantCareData
}