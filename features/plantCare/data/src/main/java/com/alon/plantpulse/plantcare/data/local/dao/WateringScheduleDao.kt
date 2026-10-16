package com.alon.plantpulse.plantcare.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alon.plantpulse.plantcare.data.local.entity.WateringScheduleEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for watering schedules.
 */
@Dao
interface WateringScheduleDao {

    /**
     * Inserts or updates a watering schedule.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: WateringScheduleEntity)

    /**
     * Streams the schedule for a specific plant.
     */
    @Query("SELECT * FROM watering_schedules WHERE plantId = :plantId")
    fun getScheduleFlow(plantId: Int): Flow<WateringScheduleEntity?>

    /**
     * Deletes the schedule for a specific plant.
     */
    @Query("DELETE FROM watering_schedules WHERE plantId = :plantId")
    suspend fun deleteSchedule(plantId: Int): Int
}
