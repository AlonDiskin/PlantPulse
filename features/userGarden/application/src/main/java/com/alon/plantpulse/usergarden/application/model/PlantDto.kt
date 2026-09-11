package com.alon.plantpulse.usergarden.application.model

import com.alon.plantpulse.usergarden.domain.PlantEntity

/**
 * Data Transfer Object for Plant entity to be used in the application layer.
 */
data class PlantDto(
    val id: Int,
    val commonName: String,
    val scientificName: String,
    val imageUrl: String,
    val isAdded: Boolean = false
)

/**
 * Mapper extension to convert PlantEntity to PlantDto.
 */
fun PlantEntity.toDto(isAdded: Boolean) = PlantDto(
    id = id,
    commonName = commonName,
    scientificName = scientificName,
    imageUrl = imageUrl,
    isAdded = isAdded
)
