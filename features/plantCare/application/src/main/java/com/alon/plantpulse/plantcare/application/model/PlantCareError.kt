package com.alon.plantpulse.plantcare.application.model

sealed class PlantCareError {

    data class Internal(val error: Throwable) : PlantCareError()

    object LightSensorNotExist : PlantCareError()
}