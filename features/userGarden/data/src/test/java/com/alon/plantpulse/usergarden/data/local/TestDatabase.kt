package com.alon.plantpulse.usergarden.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

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
