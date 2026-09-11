package com.alon.plantpulse.usergarden.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPlantDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(userPlant: UserPlant)

    @Query("SELECT * FROM user_plants")
    fun getAll(): PagingSource<Int ,UserPlant>

    @Query("SELECT plantId FROM user_plants")
    fun getAllIds(): Flow<List<Int>>
}
