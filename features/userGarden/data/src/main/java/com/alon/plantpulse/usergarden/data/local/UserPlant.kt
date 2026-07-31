package com.alon.plantpulse.usergarden.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_plants")
data class UserPlant(@PrimaryKey val plantId: Int)