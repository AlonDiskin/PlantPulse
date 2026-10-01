package com.alon.plantpulse.usergarden.featuretest.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestAppCoroutineScopesModule {

    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope = TestScope(UnconfinedTestDispatcher())
}