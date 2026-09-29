package com.alon.plantpulse.usergarden.application

import androidx.paging.PagingData
import com.alon.plantpulse.usergarden.application.interfaces.PlantRepository
import com.alon.plantpulse.usergarden.application.usecase.SearchUserPlantsUseCase
import com.alon.plantpulse.usergarden.domain.PlantEntity
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class SearchUserPlantsUseCaseTest {

    private val repository: PlantRepository = mockk()
    private val useCase = SearchUserPlantsUseCase(repository)

    @Test
    fun doNotPerformSearch_WhenSearchQueryIsEmpty() {
        // Given a use case and an empty search query
        val query = ""

        // When use case is executed with empty query
        val resultFlow = useCase(query)

        // Then use case should return empty Flow<PagingData<UserPlantDto>> with EmptySearchQuery error
        // (In Paging, the error is often encapsulated in the LoadState of PagingData)
        // We verify that search was NOT called.
        verify(exactly = 0) { repository.searchUserPlants(any()) }
    }

    @Test
    fun performUserPlantsSearch_WhenExecutedWithValidQuery() = runTest {
        // Given a use case and a non empty search query
        val query = "monstera"
        val pagingData = PagingData.from(emptyList<PlantEntity>())
        every { repository.searchUserPlants(query) } returns flowOf(pagingData)

        // When use case is executed with query
        val resultFlow = useCase(query)
        val result = resultFlow.first()

        // Then use case should perform search on plants repository with query
        verify(exactly = 1) { repository.searchUserPlants(query) }
        
        // And return repository search results flow
        assertThat(result).isNotNull()
        assertThat(result).isInstanceOf(PagingData::class.java)
    }
}
