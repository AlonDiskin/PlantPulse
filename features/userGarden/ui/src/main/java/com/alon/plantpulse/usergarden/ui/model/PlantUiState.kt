package com.alon.plantpulse.usergarden.ui.model

import com.alon.plantpulse.usergarden.application.model.PlantDto

/**
 * UI State representation of a plant for display in the UI layer.
 *
 * @property id Unique identifier for the plant.
 * @property commonName The primary name displayed to the user.
 * @property scientificName The botanical name displayed as a subtitle.
 * @property imageUrl The URL for the plant's thumbnail image.
 */
data class PlantUiState(
    val id: Int,
    val commonName: String,
    val scientificName: String,
    val imageUrl: String
)

/**
 * Extension to map [PlantDto] to [PlantUiState].
 */
fun PlantDto.toUiState() = PlantUiState(
    id = id,
    commonName = commonName,
    scientificName = scientificName,
    imageUrl = imageUrl
)
