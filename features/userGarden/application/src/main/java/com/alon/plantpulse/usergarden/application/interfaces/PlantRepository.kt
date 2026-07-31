package com.alon.plantpulse.usergarden.application.interfaces

import androidx.paging.PagingData
import com.alon.plantpulse.usergarden.application.model.Result
import com.alon.plantpulse.usergarden.application.model.UserGardenError
import com.alon.plantpulse.usergarden.domain.PlantEntity
import kotlinx.coroutines.flow.Flow

/**
 * Defines the data access operations for plants within the system.
 *
 * This repository acts as the single source of truth for both searching the global plant catalog
 * and managing the user's personal garden collection.
 */
interface PlantRepository {
    /**
     * Searches for plants in the catalog that match the specified [query].
     *
     * @param query The text search term to filter plants by name or attributes.
     * @return A [Flow] of [PagingData] containing [PlantEntity] results matching the search criteria.
     */
    fun search(query: String): Flow<PagingData<PlantEntity>>

    /**
     * Retrieves the collection of plants currently belonging to the user's garden.
     *
     * @return A [Flow] of [PagingData] representing the user's personal plant collection.
     */
    fun getUserPlants(): Flow<PagingData<PlantEntity>>

    /**
     * Persists a plant into the user's personal garden collection.
     *
     * @param id The unique identifier of the plant to be added to the garden.
     * @return A [Result] indicating success ([Unit]) or failure with a [UserGardenError].
     */
    suspend fun addUserPlant(id: Int): Result<Unit, UserGardenError>
}
