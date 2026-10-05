package com.alon.plantpulse.usergarden.featuretest.di

import android.content.Context
import androidx.room.Room
import com.alon.plantpulse.usergarden.data.local.PlantDao
import com.alon.plantpulse.usergarden.data.local.UserPlantDao
import com.alon.plantpulse.usergarden.featuretest.util.TestDatabase
import com.alon.plantpulse.usergarden.ui.controller.UserGardenNavigator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestFeatureModule {

    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope = TestScope(UnconfinedTestDispatcher())

    @Singleton
    @Provides
    fun provideTestDatabase(@ApplicationContext context: Context): TestDatabase {
        return Room.inMemoryDatabaseBuilder(context, TestDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @Singleton
    @Provides
    fun providePlantDao(db: TestDatabase): PlantDao {
        return db.plantDao()
    }

    @Singleton
    @Provides
    fun provideUserPlantDao(db: TestDatabase): UserPlantDao {
        return db.userPlantDao()
    }

    @Singleton
    @Provides
    fun provideUserGardenNavigator(): UserGardenNavigator {
        return mockk()
    }
}