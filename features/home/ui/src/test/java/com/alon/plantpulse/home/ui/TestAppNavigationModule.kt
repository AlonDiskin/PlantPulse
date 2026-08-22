package com.alon.plantpulse.home.ui

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestAppNavigationModule {

    @Singleton
    @Provides
    fun provideAppHomeNavigator(): AppHomeNavigator = object : AppHomeNavigator {
        // Use the ID from your test resource
        override fun getAppNavGraphId(): Int = R.navigation.fake_app_nav_graph
    }
}
