package com.alon.plantpulse.plantcare.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.alon.plantpulse.plantcare.data.local.converters.PlantCareConverters
import com.alon.plantpulse.plantcare.data.local.dao.WateringScheduleDao
import com.alon.plantpulse.plantcare.data.local.entity.WateringScheduleEntity

@Database(
    entities = [WateringScheduleEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(PlantCareConverters::class)
abstract class PlantCareTestDatabase : RoomDatabase() {
    abstract fun wateringScheduleDao(): WateringScheduleDao
}
