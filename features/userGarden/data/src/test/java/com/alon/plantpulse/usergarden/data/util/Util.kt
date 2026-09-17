package com.alon.plantpulse.usergarden.data.util

import com.alon.plantpulse.usergarden.data.local.Plant

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
}