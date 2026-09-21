package com.alon.plantpulse.usergarden.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Data Access Object for the plants table.
 */
@Dao
interface PlantDao {

    /**
     * Inserts a list of plants into the database.
     * If a plant with the same ID already exists, it will be replaced.
     *
     * @param plants The list of plants to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plants: List<Plant>)

    /**
     * Searches for plants by their common name or scientific name.
     *
     * @param query The search query.
     * @return A [PagingSource] for the search results.
     */
    @Query("""
        SELECT * FROM plants 
        WHERE commonName LIKE '%' || :query || '%' 
           OR scientificName LIKE '%' || :query || '%' 
        ORDER BY commonName ASC
    """)
    fun search(query: String): PagingSource<Int, Plant>

    @Query("SELECT * FROM plants WHERE id = :id")
    suspend fun getById(id: Int): Plant

    /**
     * Searches for plants specifically within the user's personal garden.
     *
     * @param query The search query.
     * @return A [PagingSource] for the search results.
     */
    @Query("""
        SELECT plants.* FROM plants 
        INNER JOIN user_plants ON plants.id = user_plants.plantId
        WHERE plants.commonName LIKE '%' || :query || '%' 
           OR plants.scientificName LIKE '%' || :query || '%' 
        ORDER BY plants.commonName ASC
    """)
    fun searchUserPlants(query: String): PagingSource<Int, Plant>
}
