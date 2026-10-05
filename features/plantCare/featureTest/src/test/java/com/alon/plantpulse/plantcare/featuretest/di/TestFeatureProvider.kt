package com.alon.plantpulse.plantcare.featuretest.di

import android.hardware.SensorManager
import com.alon.plantpulse.plantcare.api.PlantCareDataProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.mockk.mockk
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
}