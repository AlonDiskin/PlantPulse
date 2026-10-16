package com.alon.plantpulse.plantcare.application.model

import java.time.DayOfWeek

/**
 * Defines the logical repetition cycle for a watering reminder.
 * Aligned with domain-specific plant watering frequencies.
 */
sealed class RecurrencePattern {

    /**
     * No repetition. The reminder fires once and is then considered complete.
     */
    object None : RecurrencePattern()

    /**
     * Routine-based repetition.
     * The user selects specific days of the week (e.g., Mon, Wed, Fri).
     * @property daysOfWeek The set of days the reminder should fire.
     */
    data class WeeklyRoutine(
        val daysOfWeek: Set<DayOfWeek>
    ) : RecurrencePattern()

    /**
     * Frequency-based repetition.
     * Fires on a specific day, then repeats after a set number of weeks.
     * @property weeksInterval The gap between watering weeks (1 = every week, 2 = every 2 weeks, etc).
     */
    data class FixedInterval(
        val weeksInterval: WeekOffset
    ) : RecurrencePattern()

    /**
     * Type-safe options for fixed intervals, preventing invalid business states.
     */
    enum class WeekOffset(val value: Int) {
        EVERY_WEEK(1),
        EVERY_TWO_WEEKS(2),
        EVERY_THREE_WEEKS(3)
    }
}