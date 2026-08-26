package com.traffipart.polanty.core.di

import com.traffipart.polanty.core.repository.PlantIdentificationRepository
import com.traffipart.polanty.data.repository.PlantIdentificationRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindPlantIdentificationRepository(
        plantIdentificationRepositoryImpl: PlantIdentificationRepositoryImpl,
    ): PlantIdentificationRepository
}
