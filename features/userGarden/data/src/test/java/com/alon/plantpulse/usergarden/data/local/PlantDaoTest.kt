package com.alon.plantpulse.usergarden.data.local

import android.content.Context
import android.os.Looper
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.testing.asSnapshot
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alon.plantpulse.usergarden.data.util.createPlant
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
            plantDao.search("Monstera")
        }.flow

        val actualPlants: List<Plant> = pagingFlow.asSnapshot()
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then
        assertThat(actualPlants).isEqualTo(expectedResults)
    }

    @Test
    fun insertPlants_AndGetPlantById() = runTest {
        // Given
        val plants: List<Plant> = listOf(
            createPlant(1, "Monstera Deliciosa", "Monstera deliciosa", "image_url_1"),
            createPlant(2, "Snake Plant", "Sansevieria trifasciata", "image_url_2"),
            createPlant(3, "Swiss Cheese Plant", "Monstera adansonii", "image_url_3")
        )
        val plantId = plants[2].id
        val expectedPlant = plants[2]

        plantDao.insertAll(plants)
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // When
        val actualPlant = plantDao.getById(plantId)

        // Then
        assertThat(actualPlant).isEqualTo(expectedPlant)
    }

    @Test
    fun insertPlants_AndSearchForUserPlantByName() = runTest {
        // Given plants
        val userPlantDao = database.userPlantDao()
        val plants: List<Plant> = listOf(
            createPlant(1, "Monstera Deliciosa", "Monstera deliciosa", "image_url_1"),
            createPlant(2, "Snake Plant", "Sansevieria trifasciata", "image_url_2"),
            createPlant(3, "Swiss Cheese Plant", "Monstera adansonii", "image_url_3")
        )
        val expectedResults: List<Plant> = listOf(
            plants[0],
            plants[2]
        )

        // When plants are inserted to plant dao
        plantDao.insertAll(plants)
        
        // And some of them inserted to user plant dao as user plants
        userPlantDao.add(UserPlant(1))
        userPlantDao.add(UserPlant(3))
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // When plant dao is searched for plants by name that are also user plants
        val pagingFlow = Pager(
            config = PagingConfig(pageSize = 10)
        ) {
            plantDao.searchUserPlants("Monstera")
        }.flow

        val actualPlants: List<Plant> = pagingFlow.asSnapshot()
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then all matching plants paging is returned from plant dao
        assertThat(actualPlants).isEqualTo(expectedResults)
    }
}
