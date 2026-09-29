package com.alon.plantpulse.usergarden.application.model

import com.alon.plantpulse.usergarden.domain.BloomingSeason
import com.alon.plantpulse.usergarden.domain.GerminationDays
import com.alon.plantpulse.usergarden.domain.GerminationSoilTemp
import com.alon.plantpulse.usergarden.domain.MatureHeight
import com.alon.plantpulse.usergarden.domain.MatureWidth
import com.alon.plantpulse.usergarden.domain.MaturityDays
import com.alon.plantpulse.usergarden.domain.PlantCategory
import com.alon.plantpulse.usergarden.domain.PlantEntity
import com.alon.plantpulse.usergarden.domain.PlantSunCare
import com.alon.plantpulse.usergarden.domain.PlantWaterCare
import com.alon.plantpulse.usergarden.domain.RowSpacing

data class UserPlantDetailDto(val id: Int,
                              val commonName: String,
                              val scientificName: String,
                              val category: PlantCategory?,
                              val imageUrl: String,
                              val subcategory: String,
                              val daysToGermination: GerminationDays?,
                              val daysToMaturity: MaturityDays?,
                              val germinationSoilTemp: GerminationSoilTemp?,
                              val matureHeight: MatureHeight?,
                              val matureWidth: MatureWidth?,
                              val bloomSeason: BloomingSeason?,
                              val rowSpacing: RowSpacing?,
                              val sunCare: PlantSunCare?,
                              val waterCare: PlantWaterCare?,
                              val directions: String)

fun PlantEntity.toUserPlantDetailDto() = UserPlantDetailDto(
    id = id,
    commonName = commonName,
    scientificName = scientificName,
    category = category,
    imageUrl = imageUrl,
    subcategory = subcategory,
    daysToGermination = daysToGermination,
    daysToMaturity = daysToMaturity,
    germinationSoilTemp = germinationSoilTemp,
    matureHeight = matureHeight,
    matureWidth = matureWidth,
    bloomSeason = bloomSeason,
    rowSpacing = rowSpacing,
    sunCare = sunCare,
    waterCare = waterCare,
    directions = directions
)