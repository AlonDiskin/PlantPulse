package com.alon.plantpulse.plantcare.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alon.plantpulse.plantcare.application.model.AlertSettings
import com.alon.plantpulse.plantcare.application.model.RecurrencePattern
import com.alon.plantpulse.plantcare.application.model.WateringSchedule
import java.time.LocalDateTime

/**
 * Room entity representing a saved watering reminder schedule.
 */
@Entity(tableName = "watering_schedules")
data class WateringScheduleEntity(
    @PrimaryKey val plantId: Int,
    val plantName: String,
    val triggerDateTime: LocalDateTime,
    val recurrence: RecurrencePattern,
    val alertSettings: AlertSettings
)

/**
 * Maps the database entity to the application-level model.
 */
fun WateringScheduleEntity.toDomain(): WateringSchedule {
    return WateringSchedule(
        plantId = plantId,
        plantName = plantName,
        triggerDateTime = triggerDateTime,
        recurrence = recurrence,
        alertSettings = alertSettings
    )
}

/**
 * Maps the application-level model to the database entity.
 */
fun WateringSchedule.toLocal(): WateringScheduleEntity {
    return WateringScheduleEntity(
        plantId = plantId,
        plantName = plantName,
        triggerDateTime = triggerDateTime,
        recurrence = recurrence,
        alertSettings = alertSettings
    )
}
