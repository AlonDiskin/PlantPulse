package com.alon.plantpulse.usergarden.application.usecase

import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.map
import com.alon.plantpulse.usergarden.application.interfaces.PlantRepository
import com.alon.plantpulse.usergarden.application.model.PlantDto
import com.alon.plantpulse.usergarden.application.model.UserGardenError
import com.alon.plantpulse.usergarden.application.model.toDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case for searching plants.
 *
 * This class encapsulates the business logic for searching plants based on a query.
 * It ensures that the search is only performed when a non-empty query is provided.
 *
 * @property repository The repository used to fetch plant data.
 */
class SearchPlantsUseCase @Inject constructor(private val repository: PlantRepository) {

    operator fun invoke(query: String): Flow<PagingData<PlantDto>> {
        if (query.isBlank()) {
            // Return an empty flow with an error state
            val errorPagingData = PagingData.from(emptyList<PlantDto>(),
                LoadStates(LoadState.Error(UserGardenError.EmptySearchQuery()),
                    LoadState.NotLoading(false),
                    LoadState.NotLoading(false)))

            return flowOf(errorPagingData)
        }

        // Return the mapped PagingData from the repository
        return repository.search(query).map { pagingData ->
            pagingData.map { it.toDto() }
        }
    }
}
