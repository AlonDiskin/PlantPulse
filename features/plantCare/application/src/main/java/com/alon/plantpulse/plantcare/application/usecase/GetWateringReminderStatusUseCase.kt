package com.alon.plantpulse.plantcare.application.usecase

import com.alon.plantpulse.plantcare.api.PlantCareDataProvider
import com.alon.plantpulse.plantcare.application.interfaces.ReminderRepository
import com.alon.plantpulse.plantcare.application.model.PlantCareError
import com.alon.plantpulse.plantcare.application.model.ScheduleResult
import com.alon.plantpulse.plantcare.application.model.WateringReminderStatus
import com.alon.plantpulse.common.application.Result
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
 * Use case to retrieve the current status of a watering reminder for a specific plant.
 *
 * It combines static plant care data with dynamic schedule data from the local repository.
 *
 * @property reminderRepository Repository to access saved watering schedules.
 * @property api Provider for plant-specific care requirements.
 * @property defaultDispatcher Dispatcher for executing the flow operations.
 * @property mapper Mapper to convert raw API data into domain entities.
 */
class GetWateringReminderStatusUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val api: PlantCareDataProvider,
    private val defaultDispatcher: CoroutineDispatcher,
    private val mapper: PlantCareDataMapper
) {
    /**
     * Executes the process to get the watering reminder status.
     *
     * @param plantId The unique identifier of the plant.
     * @return A [Flow] emitting [Result] with [WateringReminderStatus] or [PlantCareError].
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(plantId: Int): Flow<Result<WateringReminderStatus, PlantCareError>> {
        return flow {
            // Attempt to retrieve plant care data from the provider
            val diagnosticResult = try {
                val data = api.providePlantCareData(plantId)
                val entity = mapper.mapToEntity(data)
                Result.Success(entity)
            } catch (e: Exception) {
                Result.Failure(PlantCareError.Internal(e))
            }
            emit(diagnosticResult)
        }.flatMapLatest { diagnosticResult ->
            when (diagnosticResult) {
                // Propagate errors from the data provider
                is Result.Failure -> flowOf(diagnosticResult)
                is Result.Success -> {
                    val entity = diagnosticResult.data
                    // Combine with the live schedule data from the repository
                    reminderRepository.getSchedule(plantId).map { repoResult ->
                        when (repoResult) {
                            is Result.Failure -> repoResult
                            is Result.Success -> {
                                val status = when (val scheduleResult = repoResult.data) {
                                    is ScheduleResult.Found -> {
                                        WateringReminderStatus.Scheduled(
                                            recommendedFrequency = entity.wateringFrequency,
                                            schedule = scheduleResult.schedule
                                        )
                                    }
                                    is ScheduleResult.NotFound -> {
                                        WateringReminderStatus.NotScheduled(
                                            plantId = entity.id,
                                            plantName = entity.commonName,
                                            recommendedFrequency = entity.wateringFrequency
                                        )
                                    }
                                }
                                Result.Success(status)
                            }
                        }
                    }
                }
            }
        }.flowOn(defaultDispatcher)
    }
}
