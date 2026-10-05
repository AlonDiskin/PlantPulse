package com.alon.plantpulse.di

import com.alon.plantpulse.AppHomeNavigatorImpl
import com.alon.plantpulse.UserGardenNavigatorImpl
import com.alon.plantpulse.home.ui.AppHomeNavigator
import com.alon.plantpulse.usergarden.ui.controller.UserGardenNavigator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
abstract class AppNavigationModule {

    @Binds
    @ActivityScoped
    abstract fun bindAppHomeNavigator(impl: AppHomeNavigatorImpl): AppHomeNavigator

    @Binds
    @ActivityScoped
    abstract fun bindUserGardenNavigator(impl: UserGardenNavigatorImpl): UserGardenNavigator
}