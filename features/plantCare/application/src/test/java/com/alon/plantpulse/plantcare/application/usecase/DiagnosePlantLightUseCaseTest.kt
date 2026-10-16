package com.alon.plantpulse.plantcare.application.usecase

import com.alon.plantpulse.common.application.Result
import com.alon.plantpulse.plantcare.api.PlantCareData
import com.alon.plantpulse.plantcare.api.PlantCareDataProvider
import com.alon.plantpulse.plantcare.api.PlantLight
import com.alon.plantpulse.plantcare.api.PlantWatering
import com.alon.plantpulse.plantcare.application.interfaces.LightSensorGateway
import com.alon.plantpulse.plantcare.application.model.LightStatus
import com.alon.plantpulse.plantcare.application.model.PlantCareError
import com.alon.plantpulse.plantcare.application.util.PlantCareDataMapper
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

/**
 * Unit tests for [DiagnosePlantLightUseCase] using JUnit 5.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DiagnosePlantLightUseCaseTest {

    private val lightSensorGateway: LightSensorGateway = mockk()
    private val plantCareDataProvider: PlantCareDataProvider = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()
    private val plantDataMapper = PlantCareDataMapper()
    private val useCase = DiagnosePlantLightUseCase(
        lightSensorGateway,
        plantCareDataProvider,
        testDispatcher,
        plantDataMapper
    )

    private val plantId = 1
    private val plantData = PlantCareData(
        id = plantId,
        commonName = "Snake Plant",
        light = PlantLight.SHADE, // SHADE range: 500 - 1500 Lux
        watering = PlantWatering.LOW
    )

    @Test
    fun whenInvoking_ShouldProvideDiagnosticUpdates_WhenSensorEmitsLuxValues() = runTest {
        // Given
        val luxValue = 1000f
        coEvery { plantCareDataProvider.providePlantCareData(plantId) } returns plantData
        every { lightSensorGateway.getLightLuxFlow() } returns flowOf(Result.Success(luxValue))

        // When
        val result = useCase(plantId).first()

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val data = (result as Result.Success).data
        assertThat(data.lux).isEqualTo(luxValue)
    }

    @Test
    fun whenInvoking_ShouldReturnLowStatus_WhenLuxIsBelowMin() = runTest {
        // Given
        val luxValue = 100f
        coEvery { plantCareDataProvider.providePlantCareData(plantId) } returns plantData
        every { lightSensorGateway.getLightLuxFlow() } returns flowOf(Result.Success(luxValue))

        // When
        val result = useCase(plantId).first()

        // Then
        val data = (result as Result.Success).data
        assertThat(data.status).isEqualTo(LightStatus.LOW)
    }

    @Test
    fun whenInvoking_ShouldReturnSufficientStatus_WhenLuxIsWithinRange() = runTest {
        // Given
        val luxValue = 1000f
        coEvery { plantCareDataProvider.providePlantCareData(plantId) } returns plantData
        every { lightSensorGateway.getLightLuxFlow() } returns flowOf(Result.Success(luxValue))

        // When
        val result = useCase(plantId).first()

        // Then
        val data = (result as Result.Success).data
        assertThat(data.status).isEqualTo(LightStatus.SUFFICIENT)
    }

    @Test
    fun whenInvoking_ShouldReturnTooBrightStatus_WhenLuxIsAboveMax() = runTest {
        // Given
        val luxValue = 2000f
        coEvery { plantCareDataProvider.providePlantCareData(plantId) } returns plantData
        every { lightSensorGateway.getLightLuxFlow() } returns flowOf(Result.Success(luxValue))

        // When
        val result = useCase(plantId).first()

        // Then
        val data = (result as Result.Success).data
        assertThat(data.status).isEqualTo(LightStatus.TOO_BRIGHT)
    }

    @Test
    fun whenInvoking_ShouldReturnFailure_WhenPlantDataProviderFails() = runTest {
        // Given
        val error = RuntimeException("Data provider error")
        coEvery { plantCareDataProvider.providePlantCareData(plantId) } throws error

        // When
        val result = useCase(plantId).first()

        // Then
        assertThat(result).isInstanceOf(Result.Failure::class.java)
        val failure = (result as Result.Failure).error
        assertThat(failure).isInstanceOf(PlantCareError.Internal::class.java)
        assertThat((failure as PlantCareError.Internal).error).isEqualTo(error)
    }

    @Test
    fun whenInvoking_ShouldPropagateFailure_WhenSensorGatewayFails() = runTest {
        // Given
        val error = PlantCareError.LightSensorNotExist
        coEvery { plantCareDataProvider.providePlantCareData(plantId) } returns plantData
        every { lightSensorGateway.getLightLuxFlow() } returns flowOf(Result.Failure(error))

        // When
        val result = useCase(plantId).first()

        // Then
        assertThat(result).isInstanceOf(Result.Failure::class.java)
        assertThat((result as Result.Failure).error).isEqualTo(error)
    }
}
