package com.alon.plantpulse.plantcare.application.interfaces

import com.alon.plantpulse.common.application.Result
import com.alon.plantpulse.plantcare.application.model.PlantCareError
import com.alon.plantpulse.plantcare.application.model.ScheduleResult
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining the persistence policies for plant watering schedules.
 * Implementation resides in the :data module.
 */
interface ReminderRepository {
    /**
     * Provides a continuous stream of the watering schedule status for a specific plant.
     *
     * This flow emits a new value whenever the schedule configuration for the given plant
     * is created, updated, or removed.
     *
     * @param plantId The unique identifier of the plant.
     * @return A [Flow] emitting [Result] containing [ScheduleResult] on success,
     * which explicitly indicates whether a schedule was [ScheduleResult.Found] or
     * [ScheduleResult.NotFound], or [PlantCareError] if an error occurs during retrieval.
     */
    fun getSchedule(plantId: Int): Flow<Result<ScheduleResult, PlantCareError>>
}
