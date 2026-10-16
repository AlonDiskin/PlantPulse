package com.alon.plantpulse.plantcare.application.model

sealed class NotificationSound {
    /** Uses the device's default notification sound. */
    object Default : NotificationSound()

    /**
     * Uses a specific sound selected from the device.
     * @property uriString The platform-specific identifier (URL/URI) for the sound.
     */
    data class Custom(val uriString: String) : NotificationSound()
}