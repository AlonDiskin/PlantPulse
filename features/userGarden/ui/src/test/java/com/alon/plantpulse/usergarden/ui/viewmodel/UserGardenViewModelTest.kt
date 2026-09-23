package com.alon.plantpulse.usergarden.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import com.alon.plantpulse.usergarden.application.model.UserPlantDto
import com.alon.plantpulse.usergarden.application.usecase.GetUserPlantsUseCase
import com.alon.plantpulse.usergarden.domain.BloomingSeason
import com.alon.plantpulse.usergarden.domain.PlantCategory
import com.alon.plantpulse.usergarden.domain.PlantSunCare
import com.alon.plantpulse.usergarden.domain.PlantWaterCare
import com.alon.plantpulse.usergarden.ui.model.UserPlantUiState
import com.alon.plantpulse.usergarden.ui.model.UserPlantsFiltersUiState
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
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
    private val savedStateHandle = SavedStateHandle()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun fetchUserPlants_AndFilter_WhenCreated() = runTest {
        // Given use case returns a paging data of user plants, and initial filters
        val plant1 = UserPlantDto(
            id = 1,
            commonName = "Aloe Vera",
            scientificName = "Aloe barbadensis miller",
            category = PlantCategory.HERB,
            season = BloomingSeason.PERENNIAL,
            sunCare = PlantSunCare.FULL,
            waterCare = PlantWaterCare.LOW,
            imageUrl = "url1"
        )
        val plant2 = UserPlantDto(
            id = 2,
            commonName = "Tomato",
            scientificName = "Solanum lycopersicum",
            category = PlantCategory.VEGETABLE,
            season = BloomingSeason.WARM,
            sunCare = PlantSunCare.FULL,
            waterCare = PlantWaterCare.MODERATE,
            imageUrl = "url2"
        )
        val pagingData = PagingData.from(listOf(plant1, plant2))
        
        every { mockGetUserPlantsUseCase() } returns flowOf(pagingData)

        val initialFilters = UserPlantsFiltersUiState(
            categoryFilters = setOf(PlantCategory.VEGETABLE)
        )
        val handle = SavedStateHandle(mapOf("user_garden_filters" to initialFilters))

        // When view model is created
        val viewModel = UserGardenViewModel(mockGetUserPlantsUseCase, handle)

        // Then view model should get observable user plants paging from use case
        // And filter them according to initial filters
        // And map them to ui state
        // take(1) is used because LiveData.asFlow() is an infinite stream
        val items = viewModel.userPlants.asFlow().take(1).asSnapshot()

        assertThat(items).hasSize(1)
        assertThat(items[0].commonName).isEqualTo("Tomato")
        assertThat(items[0].id).isEqualTo(2)
        
        verify { mockGetUserPlantsUseCase() }
    }

    @Test
    fun fetchUserPlants_WhenCreated() = runTest {
        // Given use case returns a paging data of user plants
        val userPlantDto = UserPlantDto(
            id = 1,
            commonName = "Aloe Vera",
            scientificName = "Aloe barbadensis miller",
            category = PlantCategory.FRUIT,
            season = BloomingSeason.COOL,
            sunCare = PlantSunCare.FULL,
            waterCare = PlantWaterCare.LOW,
            imageUrl = "url"
        )
        val pagingData = PagingData.from(listOf(userPlantDto))
        every { mockGetUserPlantsUseCase() } returns flowOf(pagingData)

        // When view model is created
        val viewModel = UserGardenViewModel(mockGetUserPlantsUseCase, savedStateHandle)

        // Then view model should get observable user plants paging from use case
        val observer = mockk<Observer<PagingData<UserPlantUiState>>>(relaxed = true)
        viewModel.userPlants.observeForever(observer)
        
        testDispatcher.scheduler.advanceUntilIdle()

        verify { mockGetUserPlantsUseCase() }

        // And map them to ui state
        val capturedData = viewModel.userPlants.value
        assertThat(capturedData).isNotNull()
        
        viewModel.userPlants.removeObserver(observer)
    }

    @Test
    fun setFiltersState_WhenCreated() {
        // Given a saved state with filters state
        val expectedFilters = UserPlantsFiltersUiState(
            categoryFilters = setOf(PlantCategory.VEGETABLE)
        )
        val handle = SavedStateHandle(mapOf("user_garden_filters" to expectedFilters))
        
        // When view model is created
        val viewModel = UserGardenViewModel(mockGetUserPlantsUseCase, handle)
        
        // Then view model should set filters state from saved state
        assertThat(viewModel.filters.value).isEqualTo(expectedFilters)
    }

    @Test
    fun filterUserPlants_WhenFiltersUpdatedByView() = runTest {
        // Given a created view model with initial empty filters
        val initialPagingData = PagingData.from(emptyList<UserPlantDto>())
        every { mockGetUserPlantsUseCase() } returns flowOf(initialPagingData)
        
        val viewModel = UserGardenViewModel(mockGetUserPlantsUseCase, SavedStateHandle())
        
        val filterObserver = mockk<Observer<UserPlantsFiltersUiState>>(relaxed = true)
        val plantsObserver = mockk<Observer<PagingData<UserPlantUiState>>>(relaxed = true)
        
        viewModel.filters.observeForever(filterObserver)
        viewModel.userPlants.observeForever(plantsObserver)
        
        val newFilters = UserPlantsFiltersUiState(
            categoryFilters = setOf(PlantCategory.HERB)
        )

        // When view model is invoked to set filters
        viewModel.setFilters(newFilters)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then view model should update filters state
        assertThat(viewModel.filters.value).isEqualTo(newFilters)
        verify { filterObserver.onChanged(newFilters) }

        // And view model should reload user plants paging from use case (due to flatMapLatest)
        verify(atLeast = 1) { mockGetUserPlantsUseCase() }
        
        // And update plants (observer should be notified with new PagingData)
        verify { plantsObserver.onChanged(any()) }
        
        viewModel.filters.removeObserver(filterObserver)
        viewModel.userPlants.removeObserver(plantsObserver)
    }

    @Test
    fun saveFiltersState_WhenFiltersUpdatedByView() {
        // Given a created view model
        val viewModel = UserGardenViewModel(mockGetUserPlantsUseCase, savedStateHandle)
        val newFilters = UserPlantsFiltersUiState(
            categoryFilters = setOf(PlantCategory.FRUIT)
        )

        // When view model is invoked to set new filters state
        viewModel.setFilters(newFilters)

        // Then view model should save filters state to saved state handle
        val savedValue = savedStateHandle.get<UserPlantsFiltersUiState>("user_garden_filters")
        assertThat(savedValue).isEqualTo(newFilters)
    }
}
