package com.traffipart.polanty.core.di

import com.traffipart.polanty.data.repository.PlantIdentificationRepositoryImpl
import com.traffipart.polanty.data.repository.PlantRepositoryImpl
import com.traffipart.polanty.data.repository.PlantSpaceRepositoryImpl
import com.traffipart.polanty.data.storage.PlantImageStorageImpl
import com.traffipart.polanty.domain.repository.PlantIdentificationRepository
import com.traffipart.polanty.domain.repository.PlantRepository
import com.traffipart.polanty.domain.repository.PlantSpaceRepository
import com.traffipart.polanty.domain.storage.PlantImageStorage
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

    @Binds
    @Singleton
    abstract fun bindPlantImageStorage(plantImageStorageImpl: PlantImageStorageImpl): PlantImageStorage

    @Binds
    @Singleton
    abstract fun bindPlantSpaceRepository(plantSpaceRepositoryImpl: PlantSpaceRepositoryImpl): PlantSpaceRepository
}
