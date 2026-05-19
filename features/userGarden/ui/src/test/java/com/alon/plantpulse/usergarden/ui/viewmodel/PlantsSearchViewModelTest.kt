package com.alon.plantpulse.usergarden.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingData
import com.alon.plantpulse.usergarden.application.model.PlantDto
import com.alon.plantpulse.usergarden.application.usecase.SearchPlantsUseCase
import com.alon.plantpulse.usergarden.ui.viewmodel.PlantsSearchViewModel
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
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
 * Unit tests for [PlantsSearchViewModel] using Robolectric.
 * Follows the UNIT TEST CASE IMPLEMENTATION PROTOCOL (TESTER).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PlantsSearchViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Test subject
    private lateinit var viewModel: PlantsSearchViewModel

    // Collaborators
    private val mockUseCase = mockk<SearchPlantsUseCase>()

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        viewModel = PlantsSearchViewModel(mockUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun fetchPagedSearchResult_WhenRequestedToPerformPlantsSearch() = runTest {
        // Given a view model with a mock search plants use case
        val query = "rose"
        val expectedPagingData = PagingData.from(listOf(PlantDto(1, "Rose", "Rosa", "url")))

        coEvery { mockUseCase(query) } returns flowOf(expectedPagingData)

        // When view model is asked to perform search
        viewModel.searchPlants(query)
        testScheduler.advanceUntilIdle()

        // Then view model should request use case to perform search
        verify { mockUseCase(query) }

        // And update the ui state when results are loaded from use case
        assertThat(viewModel.searchResults.value).isNotNull()
    }
}
