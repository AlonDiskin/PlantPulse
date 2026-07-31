package com.alon.plantpulse.usergarden.application

import androidx.paging.PagingData
import com.alon.plantpulse.usergarden.application.interfaces.PlantRepository
import com.alon.plantpulse.usergarden.application.usecase.GetUserPlantsUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetUserPlantsUseCaseTest {

    // Subject under test
    private lateinit var useCase: GetUserPlantsUseCase

    // Collaborators
    private val mockRepository: PlantRepository = mockk()

    @BeforeEach
    fun setUp() {
        useCase = GetUserPlantsUseCase(mockRepository)
    }

    @Test
    fun loadUserGardenPlants_WhenExecuted() = runTest {
        // Given
        val entities = listOf(
            createPlantEntity(1, "Rose", "Rosa", "url1"),
            createPlantEntity(2, "Desert Rose", "Adenium obesum", "url2")
        )
        every { mockRepository.getUserPlants() } returns flowOf(PagingData.from(entities))

        // When
        val resultFlow = useCase()
        val result = resultFlow.first()

        // Then
        assertThat(result).isInstanceOf(PagingData::class.java)
        verify(exactly = 1) { mockRepository.getUserPlants() }
    }
}