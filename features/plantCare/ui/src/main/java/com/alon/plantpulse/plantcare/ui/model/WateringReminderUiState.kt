package com.alon.plantpulse.plantcare.ui.model

import com.alon.plantpulse.plantcare.application.model.PlantCareError

/**
 * UI state for the Watering Reminder screen.
 *
 * @property plantName The common name of the plant.
 * @property recommendedWatering A descriptive string of the recommended watering frequency.
 * @property customMessage The user-configurable message for the notification.
 * @property selectedDate The display string for the chosen reminder date.
 * @property selectedTime The display string for the chosen reminder time.
 * @property selectedSoundName The display name of the selected notification sound.
 * @property error Any error encountered while loading or saving the reminder.
 */
data class WateringReminderUiState(
    val plantName: String = "",
    val recommendedWatering: String = "",
    var customMessage: String = "",
    val selectedDate: String = "",
    val selectedTime: String = "",
    val selectedSoundName: String = "",
    val error: PlantCareError? = null
)
