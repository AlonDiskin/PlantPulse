package com.alon.plantpulse.usergarden.di

import com.alon.plantpulse.usergarden.application.interfaces.PlantRepository
import com.alon.plantpulse.usergarden.data.impl.PlantRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Singleton
    @Binds
    abstract fun bindPlantRepository(impl: PlantRepositoryImpl): PlantRepository
}