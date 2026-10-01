package com.alon.plantpulse.usergarden.data.impl

import androidx.paging.testing.asPagingSourceFactory
import androidx.paging.testing.asSnapshot
import com.alon.plantpulse.usergarden.application.model.Result
import com.alon.plantpulse.usergarden.application.model.UserGardenError
import com.alon.plantpulse.usergarden.data.local.Plant
import com.alon.plantpulse.usergarden.data.local.PlantDao
import com.alon.plantpulse.usergarden.data.local.UserPlant
import com.alon.plantpulse.usergarden.data.local.UserPlantDao
import com.alon.plantpulse.usergarden.data.local.toPlantEntity
import com.alon.plantpulse.usergarden.data.util.createPlant
import com.alon.plantpulse.usergarden.domain.BloomingSeason
import com.alon.plantpulse.usergarden.domain.PlantCategory
import com.alon.plantpulse.usergarden.domain.PlantSunCare
import com.alon.plantpulse.usergarden.domain.PlantWaterCare
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class PlantRepositoryImplTest {

    // Test subject
    private lateinit var repo: PlantRepositoryImpl

    // Collaborators
    private val plantDao: PlantDao = mockk()
    private val userPlantDao: UserPlantDao = mockk()
    private val testScope = TestScope(UnconfinedTestDispatcher())

    @Before
    fun setup() {
        repo = PlantRepositoryImpl(plantDao, userPlantDao, testScope)
    }

    @Test
    fun returnMatchingPlantsFromDb_WhenPlantsSearchedByQuery() = runTest{
        // Given a search query
        val query = "begonia"
        val searchResults = listOf(
            Plant(1,
                "begonia",
                "begonia",
                "image_url_1",
                PlantCategory.HERB,
                "",
                null,
                null,
                null,
                null,
                null,
                BloomingSeason.WARM,
                null,
                PlantSunCare.FULL,
                PlantWaterCare.MODERATE,
                ""
            )
        )
        val expectedResults = listOf(searchResults[0].toPlantEntity())
        val searchPagingSource = searchResults.asPagingSourceFactory().invoke()

        every { plantDao.search(query) } returns searchPagingSource

        // When repo is searched for plant by query
        val actualResults = repo.search(query).asSnapshot()

        // Then matching results are returned from db
        assertThat(actualResults).isEqualTo(expectedResults)
    }

    @Test
    fun returnAllUserPlantIdsFromDb_WhenIdsQueried() = runTest{
        // Given user has plants
        val ids = listOf(1,2,3,4,5)

        coEvery { userPlantDao.getAllIds() } returns flowOf(ids)

        // When user plant ids are requested
        val actualIds = repo.getUserPlantIds().first()

        // Then user plant ids are returned from db
        assertThat(actualIds).isEqualTo(ids.toSet())
        coVerify(exactly = 1) { userPlantDao.getAllIds() }
    }

    @Test
    fun addPlantToUserPlantsDb_AndReturnSuccessResult_WhenPlantAdded() = runTest{
        // Given
        val plantId = 1
        val expectedUserPlant = UserPlant(plantId)
        val expectedResult = Result.Success(Unit)

        coEvery { userPlantDao.add(any()) } returns Unit

        // When
        val actualResult = repo.addUserPlant(plantId)

        // Then
        coVerify(exactly = 1) { userPlantDao.add(expectedUserPlant) }
        assertThat(actualResult).isEqualTo(expectedResult)
    }

    @Test
    fun returnFailureResult_WhenPlantAddingFail() = runTest{
        // Given
        val plantId = 1
        val expectedUserPlant = UserPlant(plantId)
        val error = Exception("Database error")
        val expectedResult = Result.Failure(UserGardenError.Internal(error))

        coEvery { userPlantDao.add(any()) } throws error

        // When
        val actualResult = repo.addUserPlant(plantId)

        // Then
        coVerify(exactly = 1) { userPlantDao.add(expectedUserPlant) }
        assertThat(actualResult).isEqualTo(expectedResult)
    }

    @Test
    fun getUserPlantFromDb_WhenQueriedById() = runTest{
        // Given a user plant id
        val id = 1
        val plant = createPlant(1, "Monstera Deliciosa", "Monstera deliciosa", "image_url_1")
        val plantEntity = plant.toPlantEntity()
        val expectedResult = Result.Success(plantEntity)

        coEvery { plantDao.getById(id) } returns plant

        // When user plant is queried
        val actual = repo.getUserPlant(id)

        // Then user plant is returned from local plants store
        assertThat(actual).isEqualTo(expectedResult)
        coVerify(exactly = 1) { plantDao.getById(id) }
    }

    @Test
    fun returnFailureResult_WhenRetrievingPlantFail() = runTest{
        // Given
        val plantId = 1
        val error = Exception("Database error")
        val expectedResult = Result.Failure(UserGardenError.Internal(error))

        coEvery { plantDao.getById(plantId) } throws error

        // When
        val actualResult = repo.getUserPlant(plantId)

        // Then
        coVerify(exactly = 1) { plantDao.getById(plantId) }
        assertThat(actualResult).isEqualTo(expectedResult)
    }

    @Test
    fun deleteUserPlantFromDb_WhenDeletedById() = runTest{
        // Given a user plant id
        val id = 1
        val expectedResult = Result.Success(Unit)

        coEvery { userPlantDao.delete(id) } returns 1

        // When user plant is deleted
        val actualResult = repo.deleteUserPlant(id)

        // Then user plant is deleted from local plants store
        coVerify(exactly = 1) { userPlantDao.delete(id) }
        assertThat(actualResult).isEqualTo(expectedResult)
    }

    @Test
    fun searchForMatchingUserPlants_WhenSearchedByQuery() = runTest{
        // Given a search query, plants, and user plants
        val query = "monstera"
        val matchingPlants = listOf(
            createPlant(1, "Monstera Deliciosa", "Monstera deliciosa", "url1"),
            createPlant(2, "Swiss Cheese Plant", "Monstera adansonii", "url2")
        )
        val expectedEntities = matchingPlants.map { it.toPlantEntity() }
        val pagingSource = matchingPlants.asPagingSourceFactory().invoke()

        every { plantDao.searchUserPlants(query) } returns pagingSource

        // When search for user plants is performed on repo
        val actualResults = repo.searchUserPlants(query).asSnapshot()

        // Then repo should return a paging flow of all plants, that are matched by query, and are in user plants
        assertThat(actualResults).isEqualTo(expectedEntities)
        verify(exactly = 1) { plantDao.searchUserPlants(query) }
    }
}
