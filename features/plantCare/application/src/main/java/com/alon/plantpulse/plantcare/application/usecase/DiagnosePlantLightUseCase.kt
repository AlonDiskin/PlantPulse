package com.alon.plantpulse.plantcare.application.usecase

import com.alon.plantpulse.common.application.Result
import com.alon.plantpulse.plantcare.api.PlantCareDataProvider
import com.alon.plantpulse.plantcare.application.interfaces.LightSensorGateway
import com.alon.plantpulse.plantcare.application.model.LightDiagnostic
import com.alon.plantpulse.plantcare.application.model.LightStatus
import com.alon.plantpulse.plantcare.application.model.PlantCareError
import com.alon.plantpulse.plantcare.application.util.PlantCareDataMapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case to diagnose if the current light intensity is sufficient for a specific plant.
 *
 * This use case combines real-time light sensor readings with plant-specific care requirements
 * to provide a continuous diagnostic stream.
 *
 * @property lightSensorGateway Gateway to access real-time device light sensor data.
 * @property plantCareDataProvider Provider for the care requirements of the plant being diagnosed.
 * @property defaultDispatcher Dispatcher for executing the diagnostic flow operations.
 */
class DiagnosePlantLightUseCase @Inject constructor(
    private val lightSensorGateway: LightSensorGateway,
    private val plantCareDataProvider: PlantCareDataProvider,
    private val defaultDispatcher: CoroutineDispatcher,
    private val plantDataMapper: PlantCareDataMapper
) {

    /**
     * Executes the diagnostic process for a specific plant.
     *
     * 1. Retrieves plant care data based on the provided [plantId].
     * 2. If data retrieval succeeds, starts monitoring the light sensor.
     * 3. Compares each reading against the plant's required range using domain logic.
     *
     * @param plantId The unique identifier of the plant to diagnose.
     * @return A [Flow] of [Result] containing [LightDiagnostic] updates or [PlantCareError] on failure.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(plantId: Int): Flow<Result<LightDiagnostic, PlantCareError>> {
        return flow {
            val result = try {
                Result.Success(plantCareDataProvider.providePlantCareData(plantId))
            } catch (e: Exception) {
                Result.Failure(PlantCareError.Internal(e))
            }
            emit(result)
        }.flatMapLatest { result ->
            when (result) {
                is Result.Failure -> flowOf(result)
                is Result.Success -> {
                    val plantData = result.data
                    val plant = plantDataMapper.mapToEntity(plantData)

                    lightSensorGateway.getLightLuxFlow().map { sensorResult ->
                        when (sensorResult) {
                            is Result.Success -> {
                                val lux = sensorResult.data
                                val status = when {
                                    plant.isLightLow(lux) -> LightStatus.LOW
                                    plant.isLightHigh(lux) -> LightStatus.TOO_BRIGHT
                                    else -> LightStatus.SUFFICIENT
                                }
                                Result.Success(LightDiagnostic(lux, status))
                            }
                            is Result.Failure -> sensorResult
                        }
                    }
                }
            }
        }.flowOn(defaultDispatcher)
    }
}
