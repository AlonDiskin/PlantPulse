package com.alon.plantpulse.usergarden.data.util

import com.alon.plantpulse.usergarden.data.local.Plant
import com.alon.plantpulse.usergarden.domain.BloomingSeason
import com.alon.plantpulse.usergarden.domain.PlantCategory
import com.alon.plantpulse.usergarden.domain.PlantSunCare
import com.alon.plantpulse.usergarden.domain.PlantWaterCare

fun createPlant(
    id: Int,
    commonName: String,
    scientificName: String,
    imageUrl: String
): Plant {
    return Plant(
        id = id,
        commonName = commonName,
        scientificName = scientificName,
        imageUrl = imageUrl,
        category = PlantCategory.FRUIT,
        subcategory = "",
        daysToGermination = null,
        daysToMaturity = null,
        germinationSoilTemp = null,
        matureHeight = null,
        matureWidth = null,
        bloomSeason = BloomingSeason.COOL,
        rowSpacing = null,
        sunCare = PlantSunCare.PARTIAL,
        waterCare = PlantWaterCare.HIGH,
        directions = ""
    )
}