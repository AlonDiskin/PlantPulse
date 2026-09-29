package com.alon.plantpulse.usergarden.application.model

import com.alon.plantpulse.usergarden.domain.BloomingSeason
import com.alon.plantpulse.usergarden.domain.PlantCategory
import com.alon.plantpulse.usergarden.domain.PlantEntity
import com.alon.plantpulse.usergarden.domain.PlantSunCare
import com.alon.plantpulse.usergarden.domain.PlantWaterCare

data class UserPlantDto(val id: Int,
                        val commonName: String,
                        val scientificName: String,
                        val category: PlantCategory,
                        val season: BloomingSeason,
                        val sunCare: PlantSunCare,
                        val waterCare: PlantWaterCare,
                        val imageUrl: String)

fun PlantEntity.toUserPlantDto() = UserPlantDto(
    id = id,
    commonName = commonName,
    scientificName = scientificName,
    category = category,
    season = bloomSeason,
    sunCare = sunCare,
    waterCare = waterCare,
    imageUrl = imageUrl
)