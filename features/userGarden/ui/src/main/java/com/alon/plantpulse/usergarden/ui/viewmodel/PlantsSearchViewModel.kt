package com.alon.plantpulse.usergarden.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import com.alon.plantpulse.usergarden.application.model.Result
import com.alon.plantpulse.usergarden.application.usecase.AddUserPlantUseCase
import com.alon.plantpulse.usergarden.application.usecase.SearchPlantsUseCase
import com.alon.plantpulse.usergarden.ui.model.AddPlantUiState
import com.alon.plantpulse.usergarden.ui.model.PlantUiState
import com.alon.plantpulse.usergarden.ui.model.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the plants search feature.
 */
@HiltViewModel
class PlantsSearchViewModel @Inject constructor(
    private val searchPlantsUseCase: SearchPlantsUseCase,
    private val addPlantUseCase: AddUserPlantUseCase
) : ViewModel() {

    /**
     * Trigger for the search results flow. Emitting to this flow restarts the data fetching process.
     */
    private val searchFlowTrigger = MutableSharedFlow<String>(replay = 0)

    /**
     * Trigger for the add plant flow. Emitting to this flow restarts the data fetching process.
     */
    private val addPlantFlowTrigger = MutableSharedFlow<Int>(replay = 0)

    /**
     * Observable stream of paged search results.
     */
    private val _searchResults = MutableLiveData<PagingData<PlantUiState>>()
    val searchResults: LiveData<PagingData<PlantUiState>> = _searchResults

    /**
     * Observable stream of the add plant UI state.
     */
    private val _addPlantUiState = MutableLiveData<AddPlantUiState>()
    val addPlantUiState: LiveData<AddPlantUiState> = _addPlantUiState

    init {
        createPlantsSearchChain()
        createAddPlantChain()
    }

    /**
     * Executes a search for plants based on the given [query].
     */
    fun searchPlants(query: String) {
        viewModelScope.launch {
            searchFlowTrigger.emit(query)
        }
    }

    /**
     * Adds the given [plant] to the user's garden.
     */
    fun addPlantToGarden(plant: PlantUiState) {
        viewModelScope.launch {
            addPlantFlowTrigger.emit(plant.id)
        }
    }

    /**
     * Creates the plants search operation reactive chain.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun createPlantsSearchChain() {
        viewModelScope.launch {
            searchFlowTrigger.flatMapLatest { query -> searchPlantsUseCase(query) }
                .map { pagingData -> pagingData.map { it.toUiState() } }
                .collect { pagingData ->
                    _searchResults.value = pagingData
                }
        }
    }

    /**
     * Creates the add plant operation reactive chain.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun createAddPlantChain() {
        viewModelScope.launch {
            addPlantFlowTrigger
                .flatMapConcat { id -> flowOf(addPlantUseCase(id)) }
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _addPlantUiState.value = AddPlantUiState.Success
                        }

                        is Result.Failure -> {
                            _addPlantUiState.value = AddPlantUiState.Error(result.error)
                        }
                    }
                }
        }
    }
}
