package com.alon.plantpulse.plantcare.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alon.plantpulse.common.application.Result
import com.alon.plantpulse.plantcare.application.model.*
import com.alon.plantpulse.plantcare.application.usecase.GetWateringReminderStatusUseCase
import com.alon.plantpulse.plantcare.domain.PlantWateringFrequency
import com.alon.plantpulse.plantcare.ui.model.WateringReminderUiState
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.LooperMode
import java.time.LocalDateTime

/**
 * Unit tests for [WateringReminderViewModel] using Robolectric.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
class WateringReminderViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val getStatusUseCase: GetWateringReminderStatusUseCase = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun whenCreated_WithValidIdAndNotScheduledStatus_ShouldEmitCorrectUiState() {
        // Given
        val plantId = 1
        val plantName = "Snake Plant"
        val status = WateringReminderStatus.NotScheduled(
            plantId = plantId,
            plantName = plantName,
            recommendedFrequency = PlantWateringFrequency.LOW
        )
        every { getStatusUseCase(plantId) } returns flowOf(Result.Success(status))
        val handle = SavedStateHandle(mapOf("plantId" to plantId))
        val observer = mockk<Observer<WateringReminderUiState>>(relaxed = true)

        // When
        val viewModel = WateringReminderViewModel(getStatusUseCase, handle)
        viewModel.uiState.observeForever(observer)

        // Then
        val expectedState = WateringReminderUiState(
            plantName = plantName,
            recommendedWatering = "Recommended: Every 14-21 days"
        )
        assertThat(viewModel.uiState.value).isEqualTo(expectedState)
        verify { observer.onChanged(expectedState) }
        
        viewModel.uiState.removeObserver(observer)
    }

    @Test
    fun whenCreated_WithValidIdAndScheduledStatus_ShouldEmitCorrectUiState() {
        // Given
        val plantId = 1
        val plantName = "Snake Plant"
        val schedule = WateringSchedule(
            plantId = plantId,
            plantName = plantName,
            triggerDateTime = LocalDateTime.now(),
            recurrence = RecurrencePattern.None,
            alertSettings = AlertSettings("Time to water!", NotificationSound.Default)
        )
        val status = WateringReminderStatus.Scheduled(
            recommendedFrequency = PlantWateringFrequency.LOW,
            schedule = schedule
        )
        every { getStatusUseCase(plantId) } returns flowOf(Result.Success(status))
        val handle = SavedStateHandle(mapOf("plantId" to plantId))
        val observer = mockk<Observer<WateringReminderUiState>>(relaxed = true)

        // When
        val viewModel = WateringReminderViewModel(getStatusUseCase, handle)
        viewModel.uiState.observeForever(observer)

        // Then
        val expectedState = WateringReminderUiState(
            plantName = plantName,
            recommendedWatering = "Recommended: Every 14-21 days"
        )
        assertThat(viewModel.uiState.value).isEqualTo(expectedState)
        verify { observer.onChanged(expectedState) }

        viewModel.uiState.removeObserver(observer)
    }

    @Test
    fun whenCreated_WithValidIdAndFailure_ShouldEmitUiStateWithError() {
        // Given
        val plantId = 1
        val error = PlantCareError.Internal(Exception("Failure"))
        every { getStatusUseCase(plantId) } returns flowOf(Result.Failure(error))
        val handle = SavedStateHandle(mapOf("plantId" to plantId))
        val observer = mockk<Observer<WateringReminderUiState>>(relaxed = true)

        // When
        val viewModel = WateringReminderViewModel(getStatusUseCase, handle)
        viewModel.uiState.observeForever(observer)

        // Then
        assertThat(viewModel.uiState.value?.error).isEqualTo(error)
        
        viewModel.uiState.removeObserver(observer)
    }

    @Test(expected = IllegalStateException::class)
    fun whenCreated_WithMissingPlantId_ShouldThrowException() {
        // Given
        val handle = SavedStateHandle() // No plantId

        // When
        WateringReminderViewModel(getStatusUseCase, handle)
    }
}
