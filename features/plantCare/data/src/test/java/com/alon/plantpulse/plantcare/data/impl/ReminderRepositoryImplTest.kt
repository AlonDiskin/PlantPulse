package com.alon.plantpulse.plantcare.data.impl

import app.cash.turbine.test
import com.alon.plantpulse.common.application.Result
import com.alon.plantpulse.plantcare.application.model.AlertSettings
import com.alon.plantpulse.plantcare.application.model.NotificationSound
import com.alon.plantpulse.plantcare.application.model.PlantCareError
import com.alon.plantpulse.plantcare.application.model.RecurrencePattern
import com.alon.plantpulse.plantcare.application.model.ScheduleResult
import com.alon.plantpulse.plantcare.data.local.dao.WateringScheduleDao
import com.alon.plantpulse.plantcare.data.local.entity.WateringScheduleEntity
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

/**
 * Unit tests for [ReminderRepositoryImpl] using Robolectric and Turbine for flow testing.
 */
class ReminderRepositoryImplTest {

    private val wateringScheduleDao: WateringScheduleDao = mockk()
    private lateinit var repository: ReminderRepositoryImpl

    @Before
    fun setUp() {
        repository = ReminderRepositoryImpl(wateringScheduleDao)
    }

    @Test
    fun whenQueryForSchedule_AndScheduleExist_ShouldReturnFoundResult() = runTest {
        // Given: A specific plant ID and a corresponding database entity
        val plantId = 1
        val entity = WateringScheduleEntity(
            plantId = plantId,
            plantName = "Snake Plant",
            triggerDateTime = LocalDateTime.now(),
            recurrence = RecurrencePattern.None,
            alertSettings = AlertSettings("Water me", NotificationSound.Default)
        )
        every { wateringScheduleDao.getScheduleFlow(plantId) } returns flowOf(entity)

        // When/Then: Collecting the schedule stream should yield a Success result containing the found schedule
        repository.getSchedule(plantId).test {
            val item = awaitItem()
            assertThat(item).isInstanceOf(Result.Success::class.java)
            
            val scheduleResult = (item as Result.Success).data
            assertThat(scheduleResult).isInstanceOf(ScheduleResult.Found::class.java)
            assertThat((scheduleResult as ScheduleResult.Found).schedule.plantId).isEqualTo(plantId)
            
            awaitComplete()
        }
    }

    @Test
    fun whenQueryForSchedule_AndScheduleDoNotExist_ShouldReturnNotFoundResult() = runTest {
        // Given: A plant ID with no entry in the database
        val plantId = 1
        every { wateringScheduleDao.getScheduleFlow(plantId) } returns flowOf(null)

        // When/Then: Collecting the stream should yield a Success result with ScheduleResult.NotFound
        repository.getSchedule(plantId).test {
            val item = awaitItem()
            assertThat(item).isInstanceOf(Result.Success::class.java)
            
            val scheduleResult = (item as Result.Success).data
            assertThat(scheduleResult).isEqualTo(ScheduleResult.NotFound)
            
            awaitComplete()
        }
    }

    @Test
    fun whenQueryForSchedule_AndDbThrowsException_ShouldReturnInternalError() = runTest {
        // Given: A database error occurs during the flow execution
        val plantId = 1
        val exception = RuntimeException("DB Error")
        every { wateringScheduleDao.getScheduleFlow(plantId) } returns flow { throw exception }

        // When/Then: The exception should be caught and emitted as a Failure result with internal error
        repository.getSchedule(plantId).test {
            val item = awaitItem()
            assertThat(item).isInstanceOf(Result.Failure::class.java)
            
            val error = (item as Result.Failure).error
            assertThat(error).isInstanceOf(PlantCareError.Internal::class.java)
            assertThat((error as PlantCareError.Internal).error).isEqualTo(exception)
            
            awaitComplete()
        }
    }
}
