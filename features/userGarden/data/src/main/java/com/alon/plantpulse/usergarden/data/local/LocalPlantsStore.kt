package com.alon.plantpulse.usergarden.data.local

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.alon.plantpulse.usergarden.domain.PlantEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Local data store for plants, using Room for persistence and Paging library for data loading.
 */
@Singleton
class LocalPlantsStore @Inject constructor(private val plantDao: PlantDao) {

    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
    }

    /**
     * Searches for plants locally and returns a [Flow] of [PagingData].
     *
     * @param query The search query to filter plants.
     * @return A [Flow] of paginated plant entities.
     */
    fun search(query: String): Flow<PagingData<PlantEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = DEFAULT_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { plantDao.searchPlants(query) }
        )
            .flow
            .map { pagingData -> pagingData.map { it.toPlantEntity() }}
    }
}
