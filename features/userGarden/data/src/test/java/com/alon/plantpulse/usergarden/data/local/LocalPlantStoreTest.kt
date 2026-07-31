package com.alon.plantpulse.usergarden.data.local

import androidx.paging.testing.asPagingSourceFactory
import androidx.paging.testing.asSnapshot
import com.alon.plantpulse.usergarden.application.model.Result
import com.alon.plantpulse.usergarden.application.model.UserGardenError
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class LocalPlantStoreTest {

    // Test subject
    private lateinit var store: LocalPlantsStore

    // Collaborators
    private val plantDao: PlantDao = mockk()
    private val userPlantDao: UserPlantDao = mockk()

    @Before
    fun setup() {
        store = LocalPlantsStore(plantDao,userPlantDao)
    }

    @Test
    fun delegateSearchToDb_WhenPlantSearched() = runTest {
        // Given
        val query = "begonia"
        val searchResults = listOf(
            Plant(1,
                "begonia",
                "begonia",
                "image_url_1",
                null,
                "",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                ""
            )
        )
        val expectedResults = listOf(searchResults[0].toPlantEntity())
        val searchPagingSource = searchResults.asPagingSourceFactory().invoke()

        every { plantDao.search(query) } returns searchPagingSource

        // When
        val actualResults = store.search(query).asSnapshot()

        // Then
        assertThat(actualResults).isEqualTo(expectedResults)
    }

    @Test
    fun whenRequestedToAddUserPlant_AddPlantToDb_AndReturnSuccessResult_WhenDbOperationSuccess() = runTest {
        // Given
        val plantId = 1
        val expectedUserPlant = UserPlant(plantId)
        val expectedResult = Result.Success(Unit)

        coEvery { userPlantDao.add(any()) } returns Unit

        // When
        val actualResult = store.addUserPlant(plantId)

        // Then
        coVerify(exactly = 1) { userPlantDao.add(expectedUserPlant) }
        assertThat(actualResult).isEqualTo(expectedResult)
    }

    @Test
    fun whenRequestedToAddUserPlant_AddPlantToDb_AndReturnFailureResult_WhenDbOperationFail() = runTest {
        // Given
        val plantId = 1
        val expectedUserPlant = UserPlant(plantId)
        val error = Exception("Database error")
        val expectedResult = Result.Failure(UserGardenError.Internal(error))

        coEvery { userPlantDao.add(any()) } throws error

        // When
        val actualResult = store.addUserPlant(plantId)

        // Then
        coVerify(exactly = 1) { userPlantDao.add(expectedUserPlant) }
        assertThat(actualResult).isEqualTo(expectedResult)
    }
}