package com.alon.plantpulse.di

import android.content.Context
import androidx.room.Room
import com.alon.plantpulse.db.AppDatabase
import com.alon.plantpulse.usergarden.data.local.PlantDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppDataModule {

    @Singleton
    @Provides
    fun provideTestDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .build()
    }

    @Singleton
    @Provides
    fun providePlantDao(db: AppDatabase): PlantDao {
        return db.plantDao()
    }
}