package com.alon.plantpulse.plantcare.di

import com.alon.plantpule.plantcare.device.LightSensorGatewayImpl
import com.alon.plantpulse.plantcare.application.interfaces.LightSensorGateway
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DeviceModule {
    @Singleton
    @Binds
    abstract fun bindPlantLightMeter(impl: LightSensorGatewayImpl): LightSensorGateway
}