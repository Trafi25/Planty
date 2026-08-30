package com.traffipart.polanty.core.di

import com.traffipart.polanty.data.repository.PlantIdentificationRepositoryImpl
import com.traffipart.polanty.data.repository.PlantRepositoryImpl
import com.traffipart.polanty.domain.repository.PlantIdentificationRepository
import com.traffipart.polanty.domain.repository.PlantRepository
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

    @Binds
    @Singleton
    abstract fun bindPlantRepository(repository: PlantRepositoryImpl): PlantRepository
}
