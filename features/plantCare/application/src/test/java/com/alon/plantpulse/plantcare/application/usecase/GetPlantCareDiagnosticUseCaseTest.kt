package com.alon.plantpulse.plantcare.application.usecase

import com.alon.plantpulse.common.application.Result
import com.alon.plantpulse.plantcare.api.PlantCareData
import com.alon.plantpulse.plantcare.api.PlantCareDataProvider
import com.alon.plantpulse.plantcare.api.PlantLight
import com.alon.plantpulse.plantcare.application.model.PlantCareError
import com.alon.plantpulse.plantcare.domain.PlantLightRange
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

/**
 * Unit tests for [GetPlantCareDiagnosticUseCase] using JUnit 5.
 */
class GetPlantCareDiagnosticUseCaseTest {

    private val provider: PlantCareDataProvider = mockk()
    private val useCase = GetPlantCareDiagnosticUseCase(provider)

    @Test
    fun whenInvoking_WithValidId_ShouldReturnPlantCareDiagnosticDto() = runTest {
        // Given
        val plantId = 1
        val plantData = PlantCareData(
            id = plantId,
            commonName = "Snake Plant",
            light = PlantLight.SHADE
        )
        coEvery { provider.providePlantCareData(plantId) } returns plantData

        // When
        val result = useCase(plantId)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val data = (result as Result.Success).data
        assertThat(data.commonName).isEqualTo("Snake Plant")
        assertThat(data.recommendedLightRange).isEqualTo(PlantLightRange.SHADE)
    }

    @Test
    fun whenInvoking_ShouldMapPlantLightToCorrectPlantLightRange() = runTest {
        // Given
        val mappings = mapOf(
            PlantLight.FULL to PlantLightRange.FULL,
            PlantLight.PARTIAL to PlantLightRange.PARTIAL,
            PlantLight.SHADE to PlantLightRange.SHADE
        )

        mappings.forEach { (inputLight, expectedRange) ->
            val plantId = inputLight.ordinal
            val plantData = PlantCareData(
                id = plantId,
                commonName = "Test Plant",
                light = inputLight
            )
            coEvery { provider.providePlantCareData(plantId) } returns plantData

            // When
            val result = useCase(plantId)

            // Then
            assertThat((result as Result.Success).data.recommendedLightRange).isEqualTo(expectedRange)
        }
    }

    @Test
    fun whenInvoking_ShouldReturnFailure_WhenProviderThrowsException() = runTest {
        // Given
        val plantId = 1
        val exception = RuntimeException("Data error")
        coEvery { provider.providePlantCareData(plantId) } throws exception

        // When
        val result = useCase(plantId)

        // Then
        assertThat(result).isInstanceOf(Result.Failure::class.java)
        val failure = (result as Result.Failure).error
        assertThat(failure).isInstanceOf(PlantCareError.Internal::class.java)
        assertThat((failure as PlantCareError.Internal).error).isEqualTo(exception)
    }
}
