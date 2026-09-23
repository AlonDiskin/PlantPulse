package com.alon.plantpulse.usergarden.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.alon.plantpulse.usergarden.domain.BloomingSeason
import com.alon.plantpulse.usergarden.domain.PlantCategory
import com.alon.plantpulse.usergarden.domain.PlantSunCare
import com.alon.plantpulse.usergarden.domain.PlantWaterCare
import com.alon.plantpulse.usergarden.ui.model.UserPlantsFiltersUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel responsible for managing the state of plant filters in the user's garden.
 *
 * This ViewModel handles the selection and persistence of various filter criteria such as
 * categories, seasons, and care requirements. It utilizes [SavedStateHandle] to ensure
 * filter state persists across process death.
 *
 * @property savedStateHandle Handle to saved state for state persistence.
 */
@HiltViewModel
class GardenPlantsFiltersViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    /**
     * The current UI state representing all applied garden filters.
     * Initialized from saved state if available, otherwise defaults to empty filters.
     */
    var filters: UserPlantsFiltersUiState = savedStateHandle["filters"] ?: UserPlantsFiltersUiState()

    /**
     * Updates the set of plant category filters.
     *
     * @param categoryFilters A [Set] of [PlantCategory] to filter by.
     */
    fun setPlantCategoryFilters(categoryFilters: Set<PlantCategory>) {
        filters = filters.copy(categoryFilters = categoryFilters)
    }

    /**
     * Updates the set of blooming season filters.
     *
     * @param seasonFilters A [Set] of [BloomingSeason] to filter by.
     */
    fun setPlantSeasonFilters(seasonFilters: Set<BloomingSeason>) {
        filters = filters.copy(seasonFilters = seasonFilters)
    }

    /**
     * Updates the set of sunlight requirement filters.
     *
     * @param sunCareFilters A [Set] of [PlantSunCare] to filter by.
     */
    fun setPlantSunCareFilters(sunCareFilters: Set<PlantSunCare>) {
        filters = filters.copy(sunCareFilters = sunCareFilters)
    }

    /**
     * Updates the set of watering requirement filters.
     *
     * @param waterCareFilters A [Set] of [PlantWaterCare] to filter by.
     */
    fun setPlantWaterCareFilters(waterCareFilters: Set<PlantWaterCare>) {
        filters = filters.copy(waterCareFilters = waterCareFilters)
    }

    /**
     * Resets all filter selections to their default empty state.
     */
    fun clearFilters() {
        filters = UserPlantsFiltersUiState()
    }
}
