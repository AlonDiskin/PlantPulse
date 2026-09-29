package com.alon.plantpulse.usergarden.application

import com.alon.plantpulse.usergarden.application.interfaces.PlantRepository
import com.alon.plantpulse.usergarden.application.model.Result
import com.alon.plantpulse.usergarden.application.model.UserGardenError
import com.alon.plantpulse.usergarden.application.model.toUserPlantDetailDto
import com.alon.plantpulse.usergarden.application.usecase.GetUserPlantDataUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetUserPlantDataUseCaseTest {

    // Test subject
    private lateinit var useCase: GetUserPlantDataUseCase

    // Collaborators
    private val plantRepo: PlantRepository = mockk()


    @BeforeEach
    fun setUp() {
        useCase = GetUserPlantDataUseCase(plantRepo)
    }

    @Test
    fun returnSuccessResult_WhenExecuted_AndRepoLoadPlant() = runTest {
        // Given use case is created, and a user plant id
        val id = 1
        val plant = createPlantEntity(1, "Rose", "Rosa", "url1")
        val repoResult = Result.Success(plant)
        val expectedResult = Result.Success(plant.toUserPlantDetailDto())

        coEvery { plantRepo.getUserPlant(id) } returns repoResult

        // When use case is invoked with the user plant id
        val actual = useCase(id)

        // Then use case should return loaded plant result from plant repository
        coVerify(exactly = 1) { plantRepo.getUserPlant(id) }
        assertThat(actual).isEqualTo(expectedResult)
    }

    @Test
    fun returnFailureResult_WhenExecuted_AndRepoPlantLoadFail() = runTest {
        // Given use case is created, and a user plant id
        val id = 1
        val error = UserGardenError.Internal(mockk())
        val repoResult = Result.Failure(error)
        val expectedResult = repoResult

        coEvery { plantRepo.getUserPlant(id) } returns repoResult

        // When use case is invoked with the user plant id
        val actual = useCase(id)

        // Then use case should return loaded plant result from plant repository
        coVerify(exactly = 1) { plantRepo.getUserPlant(id) }
        assertThat(actual).isEqualTo(expectedResult)
    }
}