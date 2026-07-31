package com.alon.plantpulse.usergarden.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserPlantDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(userPlant: UserPlant)

    @Query("SELECT * FROM user_plants")
    fun getAll(): PagingSource<Int ,UserPlant>
}