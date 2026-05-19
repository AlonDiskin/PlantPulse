package com.alon.plantpulse.usergarden.domain

/**
 * Represents a plant entity in the system.
 *
 * @property id The unique identifier of the plant.
 * @property commonName The commonly used name of the plant.
 * @property scientificName The botanical or scientific name of the plant.
 * @property imageUrl The URL pointing to an image of the plant.
 * @property category The general category of the plant (e.g., vegetable, fruit).
 * @property subcategory The more specific sub-classification of the plant.
 * @property daysToGermination Range of days until the seeds are expected to sprout.
 * @property daysToMaturity Range of days from planting until the plant reaches maturity.
 * @property germinationSoilTemp Soil temperature range required for successful germination (Fahrenheit).
 * @property matureHeight Expected height range of the plant at maturity (inches).
 * @property matureWidth Expected width or spread range of the plant at maturity (inches).
 * @property bloomSeason The season(s) during which the plant typically flowers.
 * @property rowSpacing Recommended distance range between plants in a row (inches).
 * @property sunCare The sunlight requirements for optimal growth.
 * @property waterCare The watering requirements for the plant.
 * @property directions General planting and care instructions.
 */
data class PlantEntity(
    val id: Int,
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
