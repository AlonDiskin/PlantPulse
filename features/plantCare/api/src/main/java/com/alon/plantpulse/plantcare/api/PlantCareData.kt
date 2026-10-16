package com.alon.plantpulse.plantcare.api

data class PlantCareData(val id: Int,
                         val commonName: String,
                         val light: PlantLight,
                         val watering: PlantWatering)
