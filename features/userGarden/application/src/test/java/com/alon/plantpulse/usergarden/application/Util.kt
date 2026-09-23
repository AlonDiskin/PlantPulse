package com.alon.plantpulse.usergarden.application

import com.alon.plantpulse.usergarden.application.model.PlantDto
import com.alon.plantpulse.usergarden.domain.BloomingSeason
import com.alon.plantpulse.usergarden.domain.PlantCategory
import com.alon.plantpulse.usergarden.domain.PlantEntity
import com.alon.plantpulse.usergarden.domain.PlantSunCare
import com.alon.plantpulse.usergarden.domain.PlantWaterCare

fun createPlantEntity(id: Int, commonName: String, scientificName: String, imageUrl: String) =
    PlantEntity(
        id = id,
        commonName = commonName,
        scientificName = scientificName,
        imageUrl = imageUrl,
        category = PlantCategory.HERB,
        subcategory = "",
        daysToGermination = null,
        daysToMaturity = null,
        germinationSoilTemp = null,
        matureHeight = null,
        matureWidth = null,
        bloomSeason = BloomingSeason.WARM,
        rowSpacing = null,
        sunCare = PlantSunCare.FULL,
        waterCare = PlantWaterCare.MODERATE,
        directions = ""
    )

fun createPlantDto(id: Int, commonName: String, scientificName: String, imageUrl: String, isAdded: Boolean) =
    PlantDto(id, commonName, scientificName, imageUrl, isAdded)