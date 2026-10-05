package com.alon.plantpulse.plantcare.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alon.plantpulse.common.application.Result
import com.alon.plantpulse.plantcare.application.model.LightDiagnostic
import com.alon.plantpulse.plantcare.application.model.LightStatus
import com.alon.plantpulse.plantcare.application.model.PlantCareDiagnosticDto
import com.alon.plantpulse.plantcare.application.model.PlantCareError
import com.alon.plantpulse.plantcare.application.usecase.DiagnosePlantLightUseCase
import com.alon.plantpulse.plantcare.application.usecase.GetPlantCareDiagnosticUseCase
import com.alon.plantpulse.plantcare.domain.PlantLightRange
import com.alon.plantpulse.plantcare.ui.model.LightMeterStatusUiState
import com.alon.plantpulse.plantcare.ui.model.PlantCareDiagnosticUiState
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
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

/**
 * Unit tests for [LightMeterViewModel] using Robolectric.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
class LightMeterViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private val diagnosePlantLightUseCase: DiagnosePlantLightUseCase = mockk()
    private val getPlantCareDiagnosticUseCase: GetPlantCareDiagnosticUseCase = mockk()
    private val plantId = 1
    private val savedStateHandle = SavedStateHandle(mapOf("plantId" to plantId))

    private lateinit var viewModel: LightMeterViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        
        // Default mocks to avoid crashes on init
        coEvery { getPlantCareDiagnosticUseCase(plantId) } returns Result.Failure(PlantCareError.Internal(Exception()))
        every { diagnosePlantLightUseCase(plantId) } returns flowOf()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun whenCreated_WithValidPlantId_ShouldLoadPlantInfoSuccessfully() {
        // Given
        val diagnosticDto = PlantCareDiagnosticDto(
            commonName = "Snake Plant",
            recommendedLightRange = PlantLightRange.SHADE
        )
        coEvery { getPlantCareDiagnosticUseCase(plantId) } returns Result.Success(diagnosticDto)
        
        val observer = mockk<Observer<PlantCareDiagnosticUiState>>(relaxed = true)

        // When
        viewModel = LightMeterViewModel(diagnosePlantLightUseCase, getPlantCareDiagnosticUseCase, savedStateHandle)
        viewModel.plantDiagnostic.observeForever(observer)

        // Then
        val expectedState = PlantCareDiagnosticUiState(
            plantName = "Snake Plant",
            recommendedRange = "500 ~ 1500 Lux"
        )
        assertThat(viewModel.plantDiagnostic.value).isEqualTo(expectedState)
        verify { observer.onChanged(expectedState) }
        
        viewModel.plantDiagnostic.removeObserver(observer)
    }

    @Test
    fun whenCreated_ShouldStartLightDiagnosis_AndProvideStatusUpdates() {
        // Given
        val luxValue = 1000f
        val diagnostic = LightDiagnostic(lux = luxValue, status = LightStatus.SUFFICIENT)
        every { diagnosePlantLightUseCase(plantId) } returns flowOf(Result.Success(diagnostic))
        
        val observer = mockk<Observer<LightMeterStatusUiState>>(relaxed = true)

        // When
        viewModel = LightMeterViewModel(diagnosePlantLightUseCase, getPlantCareDiagnosticUseCase, savedStateHandle)
        viewModel.meterStatus.observeForever(observer)

        // Then
        val expectedState = LightMeterStatusUiState(
            luxValue = "1,000",
            status = "Sufficient"
        )
        assertThat(viewModel.meterStatus.value).isEqualTo(expectedState)
        verify { observer.onChanged(expectedState) }
        
        viewModel.meterStatus.removeObserver(observer)
    }

    @Test
    fun whenPlantInfoLoadingFails_ShouldUpdatePlantDiagnosticWithError() {
        // Given
        val error = PlantCareError.Internal(Exception("Failed to load"))
        coEvery { getPlantCareDiagnosticUseCase(plantId) } returns Result.Failure(error)
        
        val observer = mockk<Observer<PlantCareDiagnosticUiState>>(relaxed = true)

        // When
        viewModel = LightMeterViewModel(diagnosePlantLightUseCase, getPlantCareDiagnosticUseCase, savedStateHandle)
        viewModel.plantDiagnostic.observeForever(observer)

        // Then
        assertThat(viewModel.plantDiagnostic.value?.error).isEqualTo(error)
        
        viewModel.plantDiagnostic.removeObserver(observer)
    }

    @Test
    fun whenLightDiagnosisFails_ShouldUpdateMeterStatusWithError() {
        // Given
        val error = PlantCareError.LightSensorNotExist
        every { diagnosePlantLightUseCase(plantId) } returns flowOf(Result.Failure(error))
        
        val observer = mockk<Observer<LightMeterStatusUiState>>(relaxed = true)

        // When
        viewModel = LightMeterViewModel(diagnosePlantLightUseCase, getPlantCareDiagnosticUseCase, savedStateHandle)
        viewModel.meterStatus.observeForever(observer)

        // Then
        assertThat(viewModel.meterStatus.value?.error).isEqualTo(error)
        
        viewModel.meterStatus.removeObserver(observer)
    }
}
