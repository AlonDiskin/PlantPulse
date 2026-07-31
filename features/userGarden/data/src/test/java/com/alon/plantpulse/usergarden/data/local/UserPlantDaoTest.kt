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
class UserPlantDaoTest {

    // System under test
    private lateinit var database: TestDatabase

    private lateinit var userPlantDao: UserPlantDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // 1. Create the in-memory database
        database = Room.inMemoryDatabaseBuilder(context, TestDatabase::class.java)
            // 2. Allow main thread queries ONLY for testing simplicity
            .allowMainThreadQueries()
            .build()
        userPlantDao = database.userPlantDao()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun insertUserPlant_AndGetAllPagedUserPlants() = runTest {
        // Given a user plant entity and an empty database
        val userPlant = UserPlant(1)
        val expectedPlants = listOf(userPlant)

        // When user plant is inserted
        userPlantDao.add(userPlant)
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // And all user plants are queried
        val pagingFlow = Pager(
            config = PagingConfig(pageSize = 10)
        ) {
            userPlantDao.getAll()
        }.flow
        val actualPlants = pagingFlow.asSnapshot()
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then resulted paged data should contain the user plant
        assertThat(actualPlants).isEqualTo(expectedPlants)
    }
}