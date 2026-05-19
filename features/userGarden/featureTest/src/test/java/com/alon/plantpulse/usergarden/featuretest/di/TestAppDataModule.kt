package com.alon.plantpulse.usergarden.featuretest.di

import android.content.Context
import androidx.room.Room
import com.alon.plantpulse.usergarden.data.local.PlantDao
import com.alon.plantpulse.usergarden.featuretest.util.TestDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestAppDataModule {

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
}