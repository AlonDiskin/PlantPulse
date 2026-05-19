package com.alon.plantpulse.usergarden.application.interfaces

import androidx.paging.PagingData
import com.alon.plantpulse.usergarden.domain.PlantEntity
import kotlinx.coroutines.flow.Flow

/**
 * Contract for plant data operations.
 */
interface PlantRepository {
    /**
     * Searches for plants matching the given query.
     */
    fun search(query: String): Flow<PagingData<PlantEntity>>
}
