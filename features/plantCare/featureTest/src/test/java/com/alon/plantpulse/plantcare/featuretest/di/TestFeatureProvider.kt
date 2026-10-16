package com.alon.plantpulse.plantcare.featuretest.di

import android.content.Context
import android.hardware.SensorManager
import androidx.room.Room
import com.alon.plantpulse.plantcare.api.PlantCareDataProvider
import com.alon.plantpulse.plantcare.data.local.dao.WateringScheduleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestFeatureProvider {
    @Provides
    @Singleton
    fun providePlantCareDataProvider(): PlantCareDataProvider {
        return mockk()
    }

    @Provides
    @Singleton
    fun provideLightSensor(): SensorManager {
        return mockk()
    }

    @Provides
    @Singleton
    fun provideDefaultDispatcher(): CoroutineDispatcher {
        return Dispatchers.Main
    }

    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope = TestScope(UnconfinedTestDispatcher())

    @Singleton
    @Provides
    fun provideTestDatabase(@ApplicationContext context: Context): PlantCareTestDatabase {
        return Room.inMemoryDatabaseBuilder(context, PlantCareTestDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @Singleton
    @Provides
    fun provideWateringScheduleDao(db: PlantCareTestDatabase): WateringScheduleDao {
        return db.wateringScheduleDao()
    }
}