package com.alon.plantpulse.usergarden.application.model

import com.alon.plantpulse.usergarden.domain.PlantEntity

/**
 * Data Transfer Object for Plant entity to be used in the application layer.
 */
data class PlantDto(
    val id: Int,
    val commonName: String,
    val scientificName: String,
    val imageUrl: String
)

/**
 * Mapper extension to convert PlantEntity to PlantDto.
 */
fun PlantEntity.toDto() = PlantDto(
    id = id,
    commonName = commonName,
    scientificName = scientificName,
    imageUrl = imageUrl
)
