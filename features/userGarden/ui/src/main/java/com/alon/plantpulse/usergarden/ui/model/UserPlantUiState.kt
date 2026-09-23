package com.alon.plantpulse.usergarden.ui.model

import com.alon.plantpulse.usergarden.application.model.UserPlantDto

/**
 * UI State representation of a user's plant for display in the garden list.
 *
 * @property id The unique identifier for the plant.
 * @property commonName The commonly used name of the plant.
 * @property scientificName The botanical or scientific name of the plant.
 * @property imageUrl The URL pointing to an image of the plant.
 * @property category The classification category of the plant.
 */
data class UserPlantUiState(
    val id: Int,
    val commonName: String,
    val scientificName: String,
    val imageUrl: String,
    val category: String
)

fun UserPlantDto.toUiState() = UserPlantUiState(
    id = id,
    commonName = commonName,
    scientificName = scientificName,
    imageUrl = imageUrl,
    category = category.name.lowercase()
)
