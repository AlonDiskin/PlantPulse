package com.alon.plantpulse.usergarden.application

import com.alon.plantpulse.usergarden.domain.PlantEntity

fun createPlantEntity(id: Int, commonName: String, scientificName: String, imageUrl: String) =
    PlantEntity(
        id = id,
        commonName = commonName,
        scientificName = scientificName,
        imageUrl = imageUrl,
        category = null,
        subcategory = "",
        daysToGermination = null,
        daysToMaturity = null,
        germinationSoilTemp = null,
        matureHeight = null,
        matureWidth = null,
        bloomSeason = null,
        rowSpacing = null,
        sunCare = null,
        waterCare = null,
        directions = ""
    )