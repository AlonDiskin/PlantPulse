package com.alon.plantpulse.usergarden.application

import androidx.paging.PagingData
import com.alon.plantpulse.usergarden.application.interfaces.PlantRepository
import com.alon.plantpulse.usergarden.application.usecase.SearchPlantsUseCase
import com.alon.plantpulse.usergarden.domain.PlantEntity
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Search Plants Use Case Tests")
class SearchPlantsUseCaseTest {

    private val repository: PlantRepository = mockk()
    private val useCase = SearchPlantsUseCase(repository)

    @Test
    @DisplayName("Given a non-empty query, when searching, then it should return flow with PagingData from repository")
    fun whenUseCaseInvoked_withNonEmptyQuery_thenShouldPerformPlantsSearch() = runTest {
        // Given
        val query = "Rose"
        val entities = listOf(
            createPlantEntity(1, "Rose", "Rosa", "url1"),
            createPlantEntity(2, "Desert Rose", "Adenium obesum", "url2")
        )
        every { repository.search(query) } returns flowOf(PagingData.from(entities))

        // When
        val resultFlow = useCase(query)
        val result = resultFlow.first()

        // Then
        assertThat(result).isInstanceOf(PagingData::class.java)
        verify(exactly = 1) { repository.search(query) }
    }

    @Test
    @DisplayName("Given an empty query, when searching, then it should return PagingData without calling repository")
    fun whenUseCaseInvoked_withEmptyQuery_thenShouldReturnPagingDataAndNotCallRepository() =
        runTest {
            // Given
            val query = ""

            // When
            val resultFlow = useCase(query)
            val result = resultFlow.first()

            // Then
            assertThat(result).isInstanceOf(PagingData::class.java)
            verify(exactly = 0) { repository.search(any()) }
        }

    @Test
    @DisplayName("Given a blank query, when searching, then it should return PagingData without calling repository")
    fun whenUseCaseInvoked_withBlankQuery_thenShouldReturnPagingDataAndNotCallRepository() =
        runTest {
            // Given
            val query = "   "

            // When
            val resultFlow = useCase(query)
            val result = resultFlow.first()

            // Then
            assertThat(result).isInstanceOf(PagingData::class.java)
            verify(exactly = 0) { repository.search(any()) }
        }

    private fun createPlantEntity(id: Int, commonName: String, scientificName: String, imageUrl: String) =
        PlantEntity(
            id = id,
            commonName = commonName,
            scientificName = scientificName,
            imageUrl = imageUrl,
            category = null,
            subcategory = "",
            daysToGermination = null,
            daysToMaturity = null,
            germinationSoilTemp = null,
            matureHeight = null,
            matureWidth = null,
            bloomSeason = null,
            rowSpacing = null,
            sunCare = null,
            waterCare = null,
            directions = ""
        )
}
