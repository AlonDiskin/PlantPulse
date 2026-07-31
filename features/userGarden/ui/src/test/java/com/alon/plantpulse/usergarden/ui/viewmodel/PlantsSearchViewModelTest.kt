package com.alon.plantpulse.usergarden.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingData
import com.alon.plantpulse.usergarden.application.model.PlantDto
import com.alon.plantpulse.usergarden.application.model.Result
import com.alon.plantpulse.usergarden.application.usecase.AddUserPlantUseCase
import com.alon.plantpulse.usergarden.application.usecase.SearchPlantsUseCase
import com.alon.plantpulse.usergarden.ui.model.AddPlantUiState
import com.alon.plantpulse.usergarden.ui.model.PlantUiState
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
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
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PlantsSearchViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Test subject
    private lateinit var viewModel: PlantsSearchViewModel

    // Collaborators
    private val mockSearchPlantsUseCase = mockk<SearchPlantsUseCase>()
    private val mocAddUserPlantUseCase = mockk<AddUserPlantUseCase>()

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        viewModel = PlantsSearchViewModel(
            mockSearchPlantsUseCase,
            mocAddUserPlantUseCase
        )
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

        coEvery { mockSearchPlantsUseCase(query) } returns flowOf(expectedPagingData)

        // When view model is asked to perform search
        viewModel.searchPlants(query)
        testScheduler.advanceUntilIdle()

        // Then view model should request use case to perform search
        verify { mockSearchPlantsUseCase(query) }

        // And update the ui state when results are loaded from use case
        assertThat(viewModel.searchResults.value).isNotNull()
    }

    @Test
    fun addPlantToUserGarden_WhenRequestedToAddPlantToGarden() = runTest {
        // Given
        val plantState = PlantUiState(1, "Rose", "Rosa", "url")
        val result = Result.Success(Unit)
        val expectedState = AddPlantUiState.Success

        coEvery { mocAddUserPlantUseCase(plantState.id) } returns result

        // When view model is asked to add plant to garden
        viewModel.addPlantToGarden(plantState)
        testScheduler.advanceUntilIdle()

        // Then view model should request use case to add plant to garden
        coVerify(exactly = 1) { mocAddUserPlantUseCase(plantState.id) }

        // And update the ui state when results are loaded from use case
        assertThat(viewModel.addPlantUiState.value).isEqualTo(expectedState)
    }
}
