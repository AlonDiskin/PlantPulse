package com.alon.plantpulse.plantcare.application.interfaces

import com.alon.plantpulse.common.application.Result
import com.alon.plantpulse.plantcare.application.model.PlantCareError
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining the contract for accessing device light sensor data.
 */
interface LightSensorGateway {
    /**
     * Provides a continuous stream of light intensity values in lux, wrapped in a [Result].
     *
     * @return A [Flow] emitting [Result] with [Float] values representing the current light intensity
     * or [PlantCareError] if the sensor is unavailable or fails.
     */
    fun getLightLuxFlow(): Flow<Result<Float, PlantCareError>>
}
