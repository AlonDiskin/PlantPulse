package com.alon.plantpulse.usergarden.data.local

import androidx.paging.testing.asPagingSourceFactory
import androidx.paging.testing.asSnapshot
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class LocalPlantStoreTest {

    // Test subject
    private lateinit var store: LocalPlantsStore

    // Collaborators
    private val dao: PlantDao = mockk()

    @Before
    fun setup() {
        store = LocalPlantsStore(dao)
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

        every { dao.searchPlants(query) } returns searchPagingSource

        // When
        val actualResults = store.search(query).asSnapshot()

        // Then
        assertThat(actualResults).isEqualTo(expectedResults)
    }
}