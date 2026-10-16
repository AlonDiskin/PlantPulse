package com.alon.plantpulse.plantcare.application.model

sealed class ScheduleResult {
    data class Found(val schedule: WateringSchedule) : ScheduleResult()
    object NotFound : ScheduleResult()
}