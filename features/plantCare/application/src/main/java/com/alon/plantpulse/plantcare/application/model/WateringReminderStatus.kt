package com.alon.plantpulse.plantcare.application.model

import com.alon.plantpulse.plantcare.domain.PlantWateringFrequency

/**
 * Represents the current state of a watering reminder for a specific plant.
 */
sealed class WateringReminderStatus {

    /**
     * Case: No reminder is currently scheduled.
     * Provides the default recommendation based on plant species logic.
     */
    data class NotScheduled(
        val plantId: Int,
        val plantName: String,
        val recommendedFrequency: PlantWateringFrequency,
    ) : WateringReminderStatus()

    /**
     * Case: A reminder is active in the system.
     * Provides the details of the next occurrence and the configuration.
     */
    data class Scheduled(
        val recommendedFrequency: PlantWateringFrequency,
        val schedule: WateringSchedule
    ) : WateringReminderStatus()
}