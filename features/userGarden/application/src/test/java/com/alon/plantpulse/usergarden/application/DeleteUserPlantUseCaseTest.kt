package com.alon.plantpulse.usergarden.application

import com.alon.plantpulse.usergarden.application.interfaces.PlantRepository
import com.alon.plantpulse.usergarden.application.model.Result
import com.alon.plantpulse.usergarden.application.usecase.DeleteUserPlantUseCase
import com.google.common.truth.Truth
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DeleteUserPlantUseCaseTest {

    // Test subject
    private lateinit var useCase: DeleteUserPlantUseCase

    // Collaborators
    private val plantRepo: PlantRepository = mockk()


    @BeforeEach
    fun setUp() {
        useCase = DeleteUserPlantUseCase(plantRepo)
    }

    @Test
    fun deleteUserPlantFromRepository_WhenExecuted() = runTest {
        // Given
        val id = 1
        val repoResult = Result.Success(Unit)

        coEvery { plantRepo.deleteUserPlant(id) } returns repoResult

        // When
        val actual = useCase(id)

        // Then
        coVerify(exactly = 1) { plantRepo.deleteUserPlant(id) }
        Truth.assertThat(actual).isEqualTo(repoResult)
    }
}