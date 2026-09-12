package com.traffipart.polanty.core.di

import android.content.Context
import androidx.room.Room
import com.traffipart.polanty.data.room.MIGRATION_2_3
import com.traffipart.polanty.data.room.PlantDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun providePlantDatabase(
        @ApplicationContext context: Context,
    ): PlantDatabase =
        Room
            .databaseBuilder(
                context,
                PlantDatabase::class.java,
                "planty.db",
            ).addMigrations(
                MIGRATION_2_3,
            ).build()

    @Provides
    @Singleton
    fun providePlantDao(plantDatabase: PlantDatabase) = plantDatabase.plantDao()

    @Provides
    @Singleton
    fun providePlantSpaceDao(plantDatabase: PlantDatabase) = plantDatabase.plantSpaceDao()

    @Provides
    @Singleton
    fun providePlantKnowledgeDao(plantDatabase: PlantDatabase) = plantDatabase.plantKnowledgeDao()
}
