package com.alon.plantpulse.usergarden.data.impl

import androidx.paging.PagingData
import com.alon.plantpulse.usergarden.application.interfaces.PlantRepository
import com.alon.plantpulse.usergarden.application.model.Result
import com.alon.plantpulse.usergarden.application.model.UserGardenError
import com.alon.plantpulse.usergarden.data.local.LocalPlantsStore
import com.alon.plantpulse.usergarden.domain.PlantEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Implementation of [PlantRepository] that manages plant data operations.
 *
 * This repository coordinates data retrieval from remote and local sources.
 *
 * @property localStore The local data source for plant information.
 */
class PlantRepositoryImpl @Inject constructor(
    private val localStore: LocalPlantsStore
) : PlantRepository {

    /**
     * Searches for plants matching the given [query].
     *
     * @param query The search term to filter plants.
     * @return A [Flow] of [PagingData] wrapping the search results.
     */
    override fun search(query: String): Flow<PagingData<PlantEntity>> {
        return localStore.search(query)
    }

    override fun getUserPlants(): Flow<PagingData<PlantEntity>> {
        return localStore.getUserPlants()
    }

    override suspend fun addUserPlant(id: Int): Result<Unit, UserGardenError> {
        return localStore.addUserPlant(id)
    }
}
