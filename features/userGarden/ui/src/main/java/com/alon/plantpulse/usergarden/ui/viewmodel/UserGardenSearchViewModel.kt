package com.alon.plantpulse.usergarden.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import com.alon.plantpulse.usergarden.application.usecase.SearchUserPlantsUseCase
import com.alon.plantpulse.usergarden.ui.model.UserPlantUiState
import com.alon.plantpulse.usergarden.ui.model.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserGardenSearchViewModel @Inject constructor(
    private val searchUserPlantsUseCase: SearchUserPlantsUseCase
) : ViewModel()  {

    private val searchFlowTrigger = MutableSharedFlow<String>(replay = 0)
    private val _searchResults = MutableLiveData<PagingData<UserPlantUiState>>()
    val searchResults: LiveData<PagingData<UserPlantUiState>> = _searchResults

    init {
        createPlantsSearchChain()
    }

    fun searchUserPlants(query: String) {
        viewModelScope.launch {
            searchFlowTrigger.emit(query)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun createPlantsSearchChain() {
        viewModelScope.launch {
            searchFlowTrigger.flatMapLatest { query -> searchUserPlantsUseCase(query) }
                .map { pagingData -> pagingData.map { it.toUiState() } }
                .collect { pagingData -> _searchResults.value = pagingData }
        }
    }
}