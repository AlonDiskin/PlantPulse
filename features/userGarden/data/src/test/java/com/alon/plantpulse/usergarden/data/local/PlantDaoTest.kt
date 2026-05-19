package com.alon.plantpulse.usergarden.data.local

import android.content.Context
import android.os.Looper
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.testing.asSnapshot
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows

@RunWith(AndroidJUnit4::class)
class PlantDaoTest {

    // System under test
    private lateinit var database: TestDatabase
    private lateinit var plantDao: PlantDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // 1. Create the in-memory database
        database = Room.inMemoryDatabaseBuilder(context, TestDatabase::class.java)
            // 2. Allow main thread queries ONLY for testing simplicity
            .allowMainThreadQueries()
            .build()
        plantDao = database.plantDao()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun returnResultsPagingSource_WhenSearched() = runTest {
        // Given
        val plants: List<Plant> = listOf(
            createPlant(1, "Monstera Deliciosa", "Monstera deliciosa", "image_url_1"),
            createPlant(2, "Snake Plant", "Sansevieria trifasciata", "image_url_2"),
            createPlant(3, "Swiss Cheese Plant", "Monstera adansonii", "image_url_3")
        )
        val expectedResults: List<Plant> = listOf(
            plants[0],
            plants[2]
        )

        plantDao.insertAll(plants)
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // When
        val pagingFlow = Pager(
            config = PagingConfig(pageSize = 10)
        ) {
            plantDao.searchPlants("Monstera")
        }.flow

        val actualPlants: List<Plant> = pagingFlow.asSnapshot()
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then
        assertThat(actualPlants).isEqualTo(expectedResults)
    }

    private fun createPlant(
        id: Int,
        commonName: String,
        scientificName: String,
        imageUrl: String
    ): Plant {
        return Plant(
            id = id,
            commonName = commonName,
            scientificName = scientificName,
            imageUrl = imageUrl,
            category = null,
            subcategory = "",
            daysToGermination = null,
            daysToMaturity = null,
            germinationSoilTemp = null,
            matureHeight = null,
            matureWidth = null,
            bloomSeason = null,
            rowSpacing = null,
            sunCare = null,
            waterCare = null,
            directions = ""
        )
    }
}
