package com.alon.plantpulse.usergarden.application.usecase

import androidx.paging.PagingData
import androidx.paging.map
import com.alon.plantpulse.usergarden.application.interfaces.PlantRepository
import com.alon.plantpulse.usergarden.application.model.UserPlantDto
import com.alon.plantpulse.usergarden.application.model.toUserPlantDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case for retrieving the list of plants in the user's garden.
 *
 * @property repository The repository to fetch the user's plants.
 */
class GetUserPlantsUseCase @Inject constructor(private val repository: PlantRepository) {

    /**
     * Executes the use case to get a flow of paginated user plants.
     *
     * @return A [Flow] of [PagingData] containing [UserPlantDto] objects.
     */
    operator fun invoke(): Flow<PagingData<UserPlantDto>> {
        return repository.getUserPlants()
            .map { pagingData -> pagingData.map { it.toUserPlantDto() } }
    }
}
