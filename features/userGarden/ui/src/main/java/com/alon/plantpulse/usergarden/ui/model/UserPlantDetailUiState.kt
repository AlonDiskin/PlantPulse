package com.alon.plantpulse.usergarden.ui.model

import com.alon.plantpulse.usergarden.application.model.UserGardenError
import com.alon.plantpulse.usergarden.application.model.UserPlantDetailDto

/**
 * UI State representation for the detailed view of a user's plant.
 *
 * This class holds the formatted data ready to be displayed in the plant detail screen.
 */
data class UserPlantDetailUiState(
    val id: Int = 0,
    val commonName: String = "",
    val scientificName: String = "",
    val imageUrl: String = "",
    val category: String = "",
    val subcategory: String = "",
    val waterCare: String = "",
    val sunCare: String = "",
    val soilTemp: String = "",
    val matureHeight: String = "",
    val maturityTime: String = "",
    val bloomSeason: String = "",
    val directions: String = "",
    val error: UserGardenError? = null
)

fun UserPlantDetailDto.toUiState(): UserPlantDetailUiState {
    return UserPlantDetailUiState(
        id = id,
        commonName = commonName,
        scientificName = scientificName,
        imageUrl = imageUrl,
        category = category?.name ?: "",
        subcategory = subcategory,
        waterCare = waterCare?.name ?: "",
        sunCare = sunCare?.name ?: "",
        soilTemp = germinationSoilTemp?.let { "${it.min}-${it.max}°F" } ?: "",
        matureHeight = matureHeight?.let { "${it.min}-${it.max} in" } ?: "",
        maturityTime = daysToMaturity?.let { "${it.min}-${it.max} days" } ?: "",
        bloomSeason = bloomSeason?.name ?: "",
        directions = directions
    )
}

fun UserGardenError.toUserPlantDetailErrorUiState(): UserPlantDetailUiState {
    return UserPlantDetailUiState(error = this)
}