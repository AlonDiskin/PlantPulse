package com.alon.plantpulse.plantcare.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.alon.plantpulse.plantcare.application.model.AlertSettings
import com.alon.plantpulse.plantcare.application.model.NotificationSound
import com.alon.plantpulse.plantcare.application.model.RecurrencePattern
import com.alon.plantpulse.plantcare.data.local.PlantCareTestDatabase
import com.alon.plantpulse.plantcare.data.local.entity.WateringScheduleEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class WateringScheduleDaoTest {

    private lateinit var database: PlantCareTestDatabase
    private lateinit var dao: WateringScheduleDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, PlantCareTestDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.wateringScheduleDao()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun insertSchedule_andGetScheduleFlow() = runTest {
        // Given
        val plantId = 1
        val schedule = WateringScheduleEntity(
            plantId = plantId,
            plantName = "Snake Plant",
            triggerDateTime = LocalDateTime.of(2023, 10, 24, 9, 0),
            recurrence = RecurrencePattern.FixedInterval(RecurrencePattern.WeekOffset.EVERY_TWO_WEEKS),
            alertSettings = AlertSettings("Water me", NotificationSound.Default)
        )

        // When
        dao.insertSchedule(schedule)

        // Then
        dao.getScheduleFlow(plantId).test {
            val emitted = awaitItem()
            assertThat(emitted).isEqualTo(schedule)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getScheduleFlow_whenNoneExists() = runTest {
        // When/Then
        dao.getScheduleFlow(999).test {
            assertThat(awaitItem()).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }
}
