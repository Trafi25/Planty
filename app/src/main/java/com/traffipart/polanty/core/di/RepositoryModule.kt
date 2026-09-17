package com.traffipart.polanty.core.di

import com.traffipart.polanty.data.repository.care.CareTaskRepositoryImpl
import com.traffipart.polanty.data.repository.knowledge.GbifPlantNameResolverImpl
import com.traffipart.polanty.data.repository.knowledge.GeminiPlantKnowledgeGenerator
import com.traffipart.polanty.data.repository.knowledge.PlantIdentificationRepositoryImpl
import com.traffipart.polanty.data.repository.knowledge.PlantKnowledgeRepositoryImpl
import com.traffipart.polanty.data.repository.plant.PlantRepositoryImpl
import com.traffipart.polanty.data.repository.plant.PlantSpaceRepositoryImpl
import com.traffipart.polanty.data.repository.plant.SpaceInitializationRepositoryImpl
import com.traffipart.polanty.data.storage.PlantImageStorageImpl
import com.traffipart.polanty.domain.PlantKnowledgeGenerator
import com.traffipart.polanty.domain.repository.care.CareTaskRepository
import com.traffipart.polanty.domain.repository.knowledge.PlantIdentificationRepository
import com.traffipart.polanty.domain.repository.knowledge.PlantKnowledgeRepository
import com.traffipart.polanty.domain.repository.knowledge.PlantNameResolver
import com.traffipart.polanty.domain.repository.plant.PlantRepository
import com.traffipart.polanty.domain.repository.plant.PlantSpaceRepository
import com.traffipart.polanty.domain.repository.plant.SpaceInitializationRepository
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

    @Binds
    @Singleton
    abstract fun bindSpaceInitializationRepository(repository: SpaceInitializationRepositoryImpl): SpaceInitializationRepository

    @Binds
    @Singleton
    abstract fun bindPlantKnowledgeRepository(repository: PlantKnowledgeRepositoryImpl): PlantKnowledgeRepository

    @Binds
    @Singleton
    abstract fun bindPlantNameResolver(resolver: GbifPlantNameResolverImpl): PlantNameResolver

    @Binds
    @Singleton
    abstract fun bindPlantKnowledgeGenerator(generator: GeminiPlantKnowledgeGenerator): PlantKnowledgeGenerator

    @Binds
    @Singleton
    abstract fun bindCareTaskRepository(repository: CareTaskRepositoryImpl): CareTaskRepository
}
