package com.alon.plantpulse.usergarden.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.alon.plantpulse.usergarden.application.usecase.GetUserPlantsUseCase
import com.alon.plantpulse.usergarden.ui.model.UserPlantUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * ViewModel for the User Garden screen.
 *
 * This ViewModel is responsible for providing the list of plants in the user's garden
 * as a stream of [PagingData]. It transforms the domain data into UI-ready state.
 */
@HiltViewModel
class UserGardenViewModel @Inject constructor(
    getUserPlantsUseCase: GetUserPlantsUseCase
) : ViewModel() {

    val userPlants: LiveData<PagingData<UserPlantUiState>> = getUserPlantsUseCase()
        .map { pagingData ->
            pagingData.map { dto ->
                UserPlantUiState(
                    id = dto.id,
                    commonName = dto.commonName,
                    scientificName = dto.scientificName,
                    imageUrl = dto.imageUrl,
                    category = dto.category
                )
            }
        }
        .cachedIn(viewModelScope)
        .asLiveData()
}
