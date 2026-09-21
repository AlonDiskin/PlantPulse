package com.alon.plantpulse.usergarden.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingData
import com.alon.plantpulse.usergarden.application.model.UserPlantDto
import com.alon.plantpulse.usergarden.application.usecase.SearchUserPlantsUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [UserGardenSearchViewModel].
 */
@OptIn(ExperimentalCoroutinesApi::class)
class UserGardenSearchViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    // Test subject
    private lateinit var viewModel: UserGardenSearchViewModel

    // Collaborators
    private val mockSearchUserPlantsUseCase = mockk<SearchUserPlantsUseCase>()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = UserGardenSearchViewModel(mockSearchUserPlantsUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadSearchResult_WhenSearchForUserPlantsPerformed() = runTest {
        // Given created view model, search query, and search results
        val query = "Aloe"
        val searchResults = UserPlantDto(
            id = 1,
            commonName = "Aloe Vera",
            scientificName = "Aloe barbadensis miller",
            category = "Succulent",
            imageUrl = "url"
        )
        val pagingData = PagingData.from(listOf(searchResults))

        every { mockSearchUserPlantsUseCase(query) } returns flowOf(pagingData)

        // When search is performed on view model
        viewModel.searchUserPlants(query)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then view model should invoke search use case with query
        verify(exactly = 1) { mockSearchUserPlantsUseCase(query) }

        // And update search results live data with expected results
        assertThat(viewModel.searchResults.value).isNotNull()
    }
}
