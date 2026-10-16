package com.alon.plantpulse.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.alon.plantpulse.plantcare.application.model.WateringSchedule
import com.alon.plantpulse.plantcare.data.local.converters.PlantCareConverters
import com.alon.plantpulse.plantcare.data.local.dao.WateringScheduleDao
import com.alon.plantpulse.plantcare.data.local.entity.WateringScheduleEntity
import com.alon.plantpulse.usergarden.data.local.Plant
import com.alon.plantpulse.usergarden.data.local.PlantConverters
import com.alon.plantpulse.usergarden.data.local.PlantDao
import com.alon.plantpulse.usergarden.data.local.UserPlant
import com.alon.plantpulse.usergarden.data.local.UserPlantDao

@Database(
    entities = [Plant::class, UserPlant::class, WateringScheduleEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(PlantConverters::class, PlantCareConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao

    abstract fun userPlantDao(): UserPlantDao

    abstract fun wateringScheduleDao(): WateringScheduleDao
}
