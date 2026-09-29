package com.alon.plantpulse.usergarden.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.alon.plantpulse.usergarden.domain.BloomingSeason
import com.alon.plantpulse.usergarden.domain.PlantCategory
import com.alon.plantpulse.usergarden.domain.PlantSunCare
import com.alon.plantpulse.usergarden.domain.PlantWaterCare
import com.alon.plantpulse.usergarden.ui.model.UserPlantsFiltersUiState
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test

class GardenPlantsFiltersViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun setCurrentFiltersState_WhenCreated() {
        // Given a UserPlantsFiltersUiState state
        val initialFilters = UserPlantsFiltersUiState(
            categoryFilters = setOf(PlantCategory.VEGETABLE, PlantCategory.HERB),
            seasonFilters = setOf(BloomingSeason.WARM),
            sunCareFilters = setOf(PlantSunCare.FULL),
            waterCareFilters = setOf(PlantWaterCare.MODERATE)
        )
        val savedStateHandle = SavedStateHandle(mapOf("filters" to initialFilters))

        // When view model is created
        val viewModel = GardenPlantsFiltersViewModel(savedStateHandle)

        // Then it should set state to current filters via SaveStateHandle
        assertThat(viewModel.filters).isEqualTo(initialFilters)
    }

    @Test
    fun changeFilters_WhenUpdated() {
        // Given a created view model with empty filters
        val viewModel = GardenPlantsFiltersViewModel(SavedStateHandle())
        
        val newCategories = setOf(PlantCategory.FRUIT, PlantCategory.BERRY)
        val newSeasons = setOf(BloomingSeason.PERENNIAL)
        val newSunCare = setOf(PlantSunCare.SHADE)
        val newWaterCare = setOf(PlantWaterCare.LOW)

        // When view model is invoked to update filters
        viewModel.setPlantCategoryFilters(newCategories)
        viewModel.setPlantSeasonFilters(newSeasons)
        viewModel.setPlantSunCareFilters(newSunCare)
        viewModel.setPlantWaterCareFilters(newWaterCare)
        
        // Then view model should update the filters state
        assertThat(viewModel.filters.categoryFilters).isEqualTo(newCategories)
        assertThat(viewModel.filters.seasonFilters).isEqualTo(newSeasons)
        assertThat(viewModel.filters.sunCareFilters).isEqualTo(newSunCare)
        assertThat(viewModel.filters.waterCareFilters).isEqualTo(newWaterCare)
    }

    @Test
    fun clearFilters_WhenCleared() {
        // Given a view model with filters
        val initialFilters = UserPlantsFiltersUiState(
            categoryFilters = setOf(PlantCategory.VEGETABLE)
        )
        val viewModel = GardenPlantsFiltersViewModel(SavedStateHandle(mapOf("filters" to initialFilters)))
        assertThat(viewModel.filters.isEmpty).isFalse()

        // When view model filters cleared
        viewModel.clearFilters()

        // Then view model should clear filters state
        assertThat(viewModel.filters.isEmpty).isTrue()
        assertThat(viewModel.filters).isEqualTo(UserPlantsFiltersUiState())
    }
}
