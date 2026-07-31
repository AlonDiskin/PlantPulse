package com.alon.plantpulse.usergarden.application

import com.alon.plantpulse.usergarden.application.interfaces.PlantRepository
import com.alon.plantpulse.usergarden.application.model.Result
import com.alon.plantpulse.usergarden.application.model.UserGardenError
import com.alon.plantpulse.usergarden.application.usecase.AddUserPlantUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AddUserPlantUseCaseTest {

    // Test subject
    private lateinit var useCase: AddUserPlantUseCase

    // Collaborators
    private val mockRepository: PlantRepository = mockk()

    @BeforeEach
    fun setUp() {
        useCase = AddUserPlantUseCase(mockRepository)
    }

    @Test
    fun whenUseCaseInvoked_withValidId_thenShouldAddPlantAndReturnSuccess() = runTest {
        // Given
        val id = 1
        val repositoryResult = mockk<Result<Unit, UserGardenError>>()
        coEvery { mockRepository.addUserPlant(id) } returns repositoryResult

        // When
        val actualResult = useCase(id)

        // Then
        assertThat(actualResult).isEqualTo(repositoryResult)
        coVerify(exactly = 1) { mockRepository.addUserPlant(id) }
    }
}
