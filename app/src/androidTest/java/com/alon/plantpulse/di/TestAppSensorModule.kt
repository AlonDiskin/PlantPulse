package com.alon.plantpulse.di

import android.hardware.SensorManager
import com.alon.plantpulse.plantcare.di.AppSensorsModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.mockk.mockk
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppSensorsModule::class]
)
object TestAppSensorModule {
    @Provides
    @Singleton
    fun provideLightSensor(): SensorManager {
        return mockk()
    }
}