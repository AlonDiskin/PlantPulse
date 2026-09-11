package com.alon.plantpulse.usergarden.ui.model

import com.alon.plantpulse.usergarden.application.model.PlantDto

/**
 * Represents the UI state for a plant item displayed in search results.
 *
 * This data class is tailored for the UI layer, providing the necessary information to render 
 * a plant item and indicate its current status within the user's garden.
 *
 * @property id The unique identifier of the plant.
 * @property commonName The primary display name of the plant.
 * @property scientificName The botanical scientific name, usually displayed as a subtitle.
 * @property imageUrl The URL pointing to the plant's thumbnail image.
 * @property isAdded A flag indicating if this plant has already been added to the user's garden collection.
 */
data class PlantUiState(
    val id: Int,
    val commonName: String,
    val scientificName: String,
    val imageUrl: String,
    val isAdded: Boolean
)

/**
 * Maps a [PlantDto] from the application layer to a [PlantUiState] for the UI layer.
 *
 * @return A new instance of [PlantUiState] populated with data from this DTO.
 */
fun PlantDto.toUiState() = PlantUiState(
    id = id,
    commonName = commonName,
    scientificName = scientificName,
    imageUrl = imageUrl,
    isAdded = isAdded
)
