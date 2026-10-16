package com.alon.plantpulse.plantcare.application.usecase

import app.cash.turbine.test
import com.alon.plantpulse.common.application.Result
import com.alon.plantpulse.plantcare.api.PlantCareData
import com.alon.plantpulse.plantcare.api.PlantCareDataProvider
import com.alon.plantpulse.plantcare.api.PlantLight
import com.alon.plantpulse.plantcare.api.PlantWatering
import com.alon.plantpulse.plantcare.application.interfaces.ReminderRepository
import com.alon.plantpulse.plantcare.application.model.*
import com.alon.plantpulse.plantcare.application.model.RecurrencePattern
import com.alon.plantpulse.plantcare.application.util.PlantCareDataMapper
import com.alon.plantpulse.plantcare.domain.PlantWateringFrequency
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

/**
 * Unit tests for [GetWateringReminderStatusUseCase] using JUnit 5.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GetWateringReminderStatusUseCaseTest {

    private val reminderRepository: ReminderRepository = mockk()
    private val api: PlantCareDataProvider = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()
    private val mapper = PlantCareDataMapper()
    private val useCase = GetWateringReminderStatusUseCase(
        reminderRepository,
        api,
        testDispatcher,
        mapper
    )

    private val plantId = 1
    private val plantName = "Snake Plant"
    private val plantCareData = PlantCareData(
        id = plantId,
        commonName = plantName,
        light = PlantLight.SHADE,
        watering = PlantWatering.LOW
    )

    @Test
    fun whenExecuting_WithValidPlantIdAndExistingSchedule_ShouldReturnScheduledStatus() = runTest {
        // Given
        val schedule = WateringSchedule(
            plantId = plantId,
            plantName = plantName,
            triggerDateTime = LocalDateTime.now(),
            recurrence = RecurrencePattern.FixedInterval(RecurrencePattern.WeekOffset.EVERY_WEEK),
            alertSettings = AlertSettings("Water me", NotificationSound.Default)
        )
        coEvery { api.providePlantCareData(plantId) } returns plantCareData
        every { reminderRepository.getSchedule(plantId) } returns flowOf(Result.Success(ScheduleResult.Found(schedule)))

        // When
        val result = useCase(plantId).first()

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val status = (result as Result.Success).data
        assertThat(status).isInstanceOf(WateringReminderStatus.Scheduled::class.java)
        val scheduled = status as WateringReminderStatus.Scheduled
        assertThat(scheduled.recommendedFrequency).isEqualTo(PlantWateringFrequency.LOW)
        assertThat(scheduled.schedule).isEqualTo(schedule)
    }

    @Test
    fun whenExecuting_WithValidPlantIdAndNoSchedule_ShouldReturnNotScheduledStatus() = runTest {
        // Given
        coEvery { api.providePlantCareData(plantId) } returns plantCareData
        every { reminderRepository.getSchedule(plantId) } returns flowOf(Result.Success(ScheduleResult.NotFound))

        // When
        val result = useCase(plantId).first()

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val status = (result as Result.Success).data
        assertThat(status).isInstanceOf(WateringReminderStatus.NotScheduled::class.java)
        val notScheduled = status as WateringReminderStatus.NotScheduled
        assertThat(notScheduled.plantName).isEqualTo(plantName)
        assertThat(notScheduled.recommendedFrequency).isEqualTo(PlantWateringFrequency.LOW)
    }

    @Test
    fun whenExecuting_WhenPlantDataProviderFails_ShouldReturnFailure() = runTest {
        // Given
        val exception = RuntimeException("API failure")
        coEvery { api.providePlantCareData(plantId) } throws exception

        // When
        val result = useCase(plantId).first()

        // Then
        assertThat(result).isInstanceOf(Result.Failure::class.java)
        val error = (result as Result.Failure).error
        assertThat(error).isInstanceOf(PlantCareError.Internal::class.java)
        assertThat((error as PlantCareError.Internal).error).isEqualTo(exception)
    }

    @Test
    fun whenExecuting_WhenScheduleRepositoryEmitsUpdates_ShouldPropagateNewStatus() = runTest {
        // Given
        coEvery { api.providePlantCareData(plantId) } returns plantCareData
        val schedule = WateringSchedule(
            plantId = plantId,
            plantName = plantName,
            triggerDateTime = LocalDateTime.now(),
            recurrence = RecurrencePattern.FixedInterval(RecurrencePattern.WeekOffset.EVERY_TWO_WEEKS),
            alertSettings = AlertSettings("Water", NotificationSound.Default)
        )
        every { reminderRepository.getSchedule(plantId) } returns flowOf(
            Result.Success(ScheduleResult.NotFound),
            Result.Success(ScheduleResult.Found(schedule))
        )

        // When/Then
        useCase(plantId).test {
            val firstItem = awaitItem()
            assertThat(firstItem).isInstanceOf(Result.Success::class.java)
            assertThat((firstItem as Result.Success).data).isInstanceOf(WateringReminderStatus.NotScheduled::class.java)

            val secondItem = awaitItem()
            assertThat(secondItem).isInstanceOf(Result.Success::class.java)
            assertThat((secondItem as Result.Success).data).isInstanceOf(WateringReminderStatus.Scheduled::class.java)

            awaitComplete()
        }
    }

    @Test
    fun whenExecuting_WhenScheduleRepositoryFails_ShouldReturnFailure() = runTest {
        // Given
        coEvery { api.providePlantCareData(plantId) } returns plantCareData
        val repoError = PlantCareError.Internal(Exception("Repo failure"))
        every { reminderRepository.getSchedule(plantId) } returns flowOf(Result.Failure(repoError))

        // When
        val result = useCase(plantId).first()

        // Then
        assertThat(result).isInstanceOf(Result.Failure::class.java)
        assertThat((result as Result.Failure).error).isEqualTo(repoError)
    }
}
