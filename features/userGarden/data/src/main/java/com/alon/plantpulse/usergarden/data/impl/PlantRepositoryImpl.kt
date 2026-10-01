package com.alon.plantpulse.usergarden.data.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.alon.plantpulse.usergarden.application.interfaces.PlantRepository
import com.alon.plantpulse.usergarden.application.model.Result
import com.alon.plantpulse.usergarden.application.model.UserGardenError
import com.alon.plantpulse.usergarden.data.local.PlantDao
import com.alon.plantpulse.usergarden.data.local.UserPlant
import com.alon.plantpulse.usergarden.data.local.UserPlantDao
import com.alon.plantpulse.usergarden.data.local.toPlantEntity
import com.alon.plantpulse.usergarden.domain.PlantEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of [PlantRepository] that manages plant data operations.
 *
 * This repository coordinates data retrieval from remote and local sources.
 *
 */
class PlantRepositoryImpl @Inject constructor(
    private val plantDao: PlantDao,
    private val userPlantDao: UserPlantDao,
    private val externalScope: CoroutineScope
) : PlantRepository {

    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
    }

    /**
     * Searches for plants matching the given [query].
     *
     * @param query The search term to filter plants.
     * @return A [Flow] of [PagingData] wrapping the search results.
     */
    override fun search(query: String): Flow<PagingData<PlantEntity>> {
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

    override fun getUserPlants(): Flow<PagingData<PlantEntity>> {
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

    override suspend fun addUserPlant(id: Int): Result<Unit, UserGardenError> {
        return externalScope.async {
            try {
                userPlantDao.add(UserPlant(id))
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Failure(UserGardenError.Internal(e))
            }
        }.await()
    }

    override fun getUserPlantIds(): Flow<Set<Int>> {
        return userPlantDao.getAllIds()
            .map { it.toSet() }
    }

    override suspend fun getUserPlant(id: Int): Result<PlantEntity, UserGardenError> {
        try {
            val plant = plantDao.getById(id)
            return Result.Success(plant.toPlantEntity())
        } catch (e: Exception) {
            return Result.Failure(UserGardenError.Internal(e))
        }
    }

    override suspend fun deleteUserPlant(id: Int): Result<Unit, UserGardenError> {
        return externalScope.async {
            try {
                userPlantDao.delete(id)
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Failure(UserGardenError.Internal(e))
            }
        }.await()
    }

    override fun searchUserPlants(query: String): Flow<PagingData<PlantEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = DEFAULT_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { plantDao.searchUserPlants(query) }
        )
            .flow
            .map { pagingData -> pagingData.map { it.toPlantEntity() } }
    }
}
