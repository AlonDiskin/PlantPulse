package com.alon.plantpulse.plantcare.di

import com.alon.plantpulse.plantcare.application.interfaces.ReminderRepository
import com.alon.plantpulse.plantcare.data.impl.ReminderRepositoryImpl
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
    abstract fun bindPlantCareRepository(impl: ReminderRepositoryImpl): ReminderRepository

}