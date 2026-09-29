package com.alon.plantpulse.usergarden.application.usecase

import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.map
import com.alon.plantpulse.usergarden.application.interfaces.PlantRepository
import com.alon.plantpulse.usergarden.application.model.UserGardenError
import com.alon.plantpulse.usergarden.application.model.UserPlantDto
import com.alon.plantpulse.usergarden.application.model.toUserPlantDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case for searching plants within the user's garden.
 */
class SearchUserPlantsUseCase @Inject constructor(
    private val repository: PlantRepository
) {

    operator fun invoke(query: String): Flow<PagingData<UserPlantDto>> {
        if (query.isBlank()) {
            val errorPagingData = PagingData.from(emptyList<UserPlantDto>(),
                LoadStates(LoadState.Error(UserGardenError.EmptySearchQuery()),
                    LoadState.NotLoading(false),
                    LoadState.NotLoading(false)))

            return flowOf(errorPagingData)
        }
        
        return repository.searchUserPlants(query)
            .map { pagingData ->
                pagingData.map { it.toUserPlantDto() }
            }
    }
}
