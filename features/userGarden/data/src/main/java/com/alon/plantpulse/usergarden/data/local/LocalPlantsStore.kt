package com.alon.plantpulse.usergarden.data.local

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.alon.plantpulse.usergarden.application.model.Result
import com.alon.plantpulse.usergarden.application.model.UserGardenError
import com.alon.plantpulse.usergarden.domain.PlantEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Local data store for plants, using Room for persistence and Paging library for data loading.
 */
@Singleton
class LocalPlantsStore @Inject constructor(private val plantDao: PlantDao,
                                           private val userPlantDao: UserPlantDao) {

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
            pagingSourceFactory = { plantDao.search(query) }
        )
            .flow
            .map { pagingData -> pagingData.map { it.toPlantEntity() }}
    }

    fun getUserPlants(): Flow<PagingData<PlantEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = DEFAULT_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { userPlantDao.getAll() }
        )
            .flow
            .map { pagingData -> pagingData.map { plantDao.getById(it.plantId).toPlantEntity() } }
    }

    suspend fun addUserPlant(id: Int): Result<Unit, UserGardenError> {
        try {
            userPlantDao.add(UserPlant(id))
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Failure(UserGardenError.Internal(e))
        }
    }
}
