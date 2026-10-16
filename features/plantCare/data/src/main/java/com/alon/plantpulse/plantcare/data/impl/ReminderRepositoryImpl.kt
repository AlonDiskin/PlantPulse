package com.alon.plantpulse.plantcare.data.impl

import com.alon.plantpulse.common.application.Result
import com.alon.plantpulse.plantcare.application.interfaces.ReminderRepository
import com.alon.plantpulse.plantcare.application.model.PlantCareError
import com.alon.plantpulse.plantcare.application.model.ScheduleResult
import com.alon.plantpulse.plantcare.data.local.dao.WateringScheduleDao
import com.alon.plantpulse.plantcare.data.local.entity.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of [ReminderRepository] that manages watering schedules using a local Room database.
 *
 * @property wateringScheduleDao The DAO for accessing watering schedule data.
 */
class ReminderRepositoryImpl @Inject constructor(
    private val wateringScheduleDao: WateringScheduleDao
) : ReminderRepository {

    override fun getSchedule(plantId: Int): Flow<Result<ScheduleResult, PlantCareError>> {
        return wateringScheduleDao.getScheduleFlow(plantId)
            .map { entity ->
                val result = if (entity != null) {
                    ScheduleResult.Found(entity.toDomain())
                } else {
                    ScheduleResult.NotFound
                }
                Result.Success(result) as Result<ScheduleResult, PlantCareError>
            }
            .catch { e ->
                emit(Result.Failure(PlantCareError.Internal(e)))
            }
    }
}
