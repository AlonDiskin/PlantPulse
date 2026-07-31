package com.alon.plantpulse.usergarden.application.model

import com.alon.plantpulse.usergarden.domain.PlantEntity

data class UserPlantDto(val id: Int,
                        val commonName: String,
                        val scientificName: String,
                        val category: String,
                        val imageUrl: String)

fun PlantEntity.toUserPlantDto() = UserPlantDto(
    id = id,
    commonName = commonName,
    scientificName = scientificName,
    category = category?.name ?: "",
    imageUrl = imageUrl
)