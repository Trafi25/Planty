package com.traffipart.polanty.data.repository

import com.traffipart.polanty.data.mapper.toDomain
import com.traffipart.polanty.data.mapper.toEntity
import com.traffipart.polanty.data.room.plant.PlantDao
import com.traffipart.polanty.domain.model.Plant
import com.traffipart.polanty.domain.repository.PlantRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PlantRepositoryImpl
    @Inject
    constructor(
        private val plantDao: PlantDao,
    ) : PlantRepository {
        override fun observePlants(): Flow<List<Plant>> =
            plantDao.observePlants().map { plants ->
                plants.map { it.toDomain() }
            }

        override suspend fun savePlant(plant: Plant): Long = plantDao.insertPlant(plant.toEntity())

        override suspend fun deletePlant(plant: Plant) {
            plantDao.deletePlant(plant.toEntity())
        }

        override fun observePlant(plantId: Long): Flow<Plant?> =
            plantDao.observePlant(plantId).map { entity ->
                entity?.toDomain()
            }
    }
