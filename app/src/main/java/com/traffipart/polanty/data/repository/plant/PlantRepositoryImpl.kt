package com.traffipart.polanty.data.repository.plant

import android.util.Log
import com.traffipart.polanty.data.mapper.toDomain
import com.traffipart.polanty.data.mapper.toEntity
import com.traffipart.polanty.data.room.plant.PlantDao
import com.traffipart.polanty.domain.model.Plant
import com.traffipart.polanty.domain.repository.plant.PlantRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG = "PlantRepo"

class PlantRepositoryImpl
    @Inject
    constructor(
        private val plantDao: PlantDao,
    ) : PlantRepository {
        override fun observePlants(): Flow<List<Plant>> =
            plantDao.observePlants().map { plants ->
                plants.map { it.toDomain() }
            }

        override fun observePlantsBySpace(spaceId: Long): Flow<List<Plant>> =
            plantDao.observePlantsBySpace(spaceId).map { plants -> plants.map { it.toDomain() } }

        override suspend fun savePlant(plant: Plant): Long =
            try {
                plantDao.insertPlant(plant.toEntity())
            } catch (e: Exception) {
                Log.e(TAG, "Error saving plant", e)
                throw e
            }

        override suspend fun deletePlant(plant: Plant) {
            try {
                plantDao.deletePlant(plant.toEntity())
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting plant", e)
                throw e
            }
        }

        override fun observePlant(plantId: Long): Flow<Plant?> =
            plantDao.observePlant(plantId).map { entity ->
                entity?.toDomain()
            }
    }
