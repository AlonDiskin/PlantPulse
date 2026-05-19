package com.alon.plantpulse.usergarden.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import com.alon.plantpulse.usergarden.application.usecase.SearchPlantsUseCase
import com.alon.plantpulse.usergarden.ui.model.PlantUiState
import com.alon.plantpulse.usergarden.ui.model.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the plants search feature.
 */
@HiltViewModel
class PlantsSearchViewModel @Inject constructor(
    private val searchPlantsUseCase: SearchPlantsUseCase
) : ViewModel() {

    /**
     * Trigger for the search results flow. Emitting to this flow restarts the data fetching process.
     */
    private val searchFlowTrigger = MutableSharedFlow<String>(replay = 0)

    /**
     * Observable stream of paged search results.
     */
    private val _searchResults = MutableLiveData<PagingData<PlantUiState>>()
    val searchResults: LiveData<PagingData<PlantUiState>> = _searchResults

    init {
        createPlantsSearchChain()
    }

    /**
     * Executes a search for plants based on the given [query].
     */
    fun searchPlants(query: String) {
        viewModelScope.launch {
            searchFlowTrigger.emit(query)
        }
    }

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
}
