package com.alon.plantpulse.plantcare.application.model

import java.time.LocalDateTime

data class WateringSchedule(val plantId: Int,
                            val plantName: String,
                            val triggerDateTime: LocalDateTime,
                            val recurrence: RecurrencePattern,
                            val alertSettings: AlertSettings
)
