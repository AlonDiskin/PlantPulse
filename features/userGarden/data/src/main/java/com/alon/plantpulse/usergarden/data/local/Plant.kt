package com.alon.plantpulse.usergarden.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alon.plantpulse.usergarden.domain.*

/**
 * Room entity representing a plant in the local database.
 * This class mirrors [PlantEntity] to maintain consistency between layers.
 */
@Entity(tableName = "plants")
data class Plant(
    @PrimaryKey val id: Int,
    val commonName: String,
    val scientificName: String,
    val imageUrl: String,
    val category: PlantCategory?,
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
    val directions: String
)

/**
 * Maps the local [Plant] entity to the domain [PlantEntity].
 */
fun Plant.toPlantEntity(): PlantEntity {
    return PlantEntity(
        id = id,
        commonName = commonName,
        scientificName = scientificName,
        imageUrl = imageUrl,
        category = category,
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
}

/**
 * Maps the domain [PlantEntity] to the local [Plant] entity.
 */
fun PlantEntity.toLocal(): Plant {
    return Plant(
        id = id,
        commonName = commonName,
        scientificName = scientificName,
        imageUrl = imageUrl,
        category = category,
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
}
