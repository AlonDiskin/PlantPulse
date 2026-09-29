package com.alon.plantpulse.usergarden.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.alon.plantpulse.usergarden.application.model.UserPlantDto
import com.alon.plantpulse.usergarden.application.usecase.GetUserPlantsUseCase
import com.alon.plantpulse.usergarden.ui.model.UserPlantUiState
import com.alon.plantpulse.usergarden.ui.model.UserPlantsFiltersUiState
import com.alon.plantpulse.usergarden.ui.model.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * ViewModel for the User Garden screen.
 *
 * This ViewModel manages the display of the user's personal plant collection,
 * providing support for paginated loading, filtering, and state persistence.
 *
 * @property getUserPlantsUseCase Use case to retrieve the user's plants.
 * @property savedStateHandle Handle to save and restore state, used for persisting filters.
 */
@HiltViewModel
class UserGardenViewModel @Inject constructor(
    private val getUserPlantsUseCase: GetUserPlantsUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        /** Key used for saving/restoring the filters state in [SavedStateHandle]. */
        private const val KEY_FILTERS = "user_garden_filters"
    }

    private val _filtersFlow = savedStateHandle.getStateFlow(
        key = KEY_FILTERS,
        initialValue = UserPlantsFiltersUiState()
    )

    /**
     * Observable [LiveData] representing the current active filters for the garden list.
     */
    val filters: LiveData<UserPlantsFiltersUiState> = _filtersFlow.asLiveData()

    /**
     * A stream of paginated plant data filtered by the current [filters] state.
     *
     * The filtration logic implements an "OR" relationship within groups (e.g., multiple categories)
     * and an "AND" relationship between different groups (e.g., Category AND Sun Care).
     * The results are cached in the [viewModelScope] to survive configuration changes.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val userPlants: LiveData<PagingData<UserPlantUiState>> = _filtersFlow
        .flatMapLatest { activeFilters ->
            getUserPlantsUseCase().map { pagingData ->
                    pagingData
                        .filter { dto ->
                            // Enforce OR within groups, AND between groups logic.
                            // If a group's Set is empty, the check falls back to true,
                            // ensuring no filtration occurs for that criteria.
                            val matchesCategory = activeFilters.categoryFilters.isEmpty() ||
                                    activeFilters.categoryFilters.contains(dto.category)

                            val matchesSeason = activeFilters.seasonFilters.isEmpty() ||
                                    activeFilters.seasonFilters.contains(dto.season)

                            val matchesSunCare = activeFilters.sunCareFilters.isEmpty() ||
                                    activeFilters.sunCareFilters.contains(dto.sunCare)

                            val matchesWaterCare = activeFilters.waterCareFilters.isEmpty() ||
                                    activeFilters.waterCareFilters.contains(dto.waterCare)

                            // Plant must satisfy all active facet categories
                            matchesCategory && matchesSeason && matchesSunCare && matchesWaterCare
                        }
                        .map(UserPlantDto::toUiState)
                }
    }
    .cachedIn(viewModelScope)
    .asLiveData()

    /**
     * Updates the current filter configuration.
     *
     * @param newFilters The new set of filters to be applied to the garden list.
     */
    fun setFilters(newFilters: UserPlantsFiltersUiState) {
        savedStateHandle[KEY_FILTERS] = newFilters
    }
}
