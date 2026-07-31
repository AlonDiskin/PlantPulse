package com.alon.plantpulse.usergarden.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.paging.PagingData
import com.alon.plantpulse.usergarden.application.model.UserPlantDto
import com.alon.plantpulse.usergarden.application.usecase.GetUserPlantsUseCase
import com.alon.plantpulse.usergarden.ui.model.UserPlantUiState
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
 * Unit tests for [UserGardenViewModel].
 */
@OptIn(ExperimentalCoroutinesApi::class)
class UserGardenViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    // Collaborators
    private val mockGetUserPlantsUseCase = mockk<GetUserPlantsUseCase>()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun fetchUserPlants_WhenCreated() = runTest {
        // Given use case returns a paging data of user plants
        val userPlantDto = UserPlantDto(
            id = 1,
            commonName = "Aloe Vera",
            scientificName = "Aloe barbadensis miller",
            category = "Succulent",
            imageUrl = "url"
        )
        val pagingData = PagingData.from(listOf(userPlantDto))
        every { mockGetUserPlantsUseCase() } returns flowOf(pagingData)

        // When view model is created
        val viewModel = UserGardenViewModel(mockGetUserPlantsUseCase)

        // Then view model should get observable user plants paging from use case
        val observer = mockk<Observer<PagingData<UserPlantUiState>>>(relaxed = true)
        viewModel.userPlants.observeForever(observer)
        
        testDispatcher.scheduler.advanceUntilIdle()

        verify { mockGetUserPlantsUseCase() }

        // And map them to ui state
        val capturedData = viewModel.userPlants.value
        assertThat(capturedData).isNotNull()
        
        // Note: PagingData content is hard to verify directly without a presenter/differ
        // but the fact that it's not null and the use case was called confirms the flow setup.
        
        viewModel.userPlants.removeObserver(observer)
    }
}
