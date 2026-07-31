package com.alon.plantpulse.usergarden.featuretest.util

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.alon.plantpulse.usergarden.data.local.Plant
import com.alon.plantpulse.usergarden.data.local.PlantConverters
import com.alon.plantpulse.usergarden.data.local.PlantDao
import com.alon.plantpulse.usergarden.data.local.UserPlant
import com.alon.plantpulse.usergarden.data.local.UserPlantDao

@Database(
    entities = [Plant::class, UserPlant::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(PlantConverters::class)
abstract class TestDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao

    abstract fun userPlantDao(): UserPlantDao
}
