package com.alon.plantpulse.di

import com.alon.plantpulse.AppHomeNavigatorImpl
import com.alon.plantpulse.home.ui.AppHomeNavigator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppNavigationModule {

    @Binds
    @Singleton
    abstract fun bindAppHomeNavigator(impl: AppHomeNavigatorImpl): AppHomeNavigator
}