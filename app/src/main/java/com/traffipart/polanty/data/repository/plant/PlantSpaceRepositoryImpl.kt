package com.traffipart.polanty.data.repository.plant

import android.util.Log
import com.traffipart.polanty.data.mapper.toDomain
import com.traffipart.polanty.data.mapper.toEntity
import com.traffipart.polanty.data.room.space.PlantSpaceDao
import com.traffipart.polanty.domain.model.PlantSpace
import com.traffipart.polanty.domain.repository.plant.PlantSpaceRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val TAG = "PlantSpaceRepo"

class PlantSpaceRepositoryImpl
    @Inject
    constructor(
        private val dao: PlantSpaceDao,
    ) : PlantSpaceRepository {
        override fun observeSpaces(): Flow<List<PlantSpace>> =
            dao.observeSpaces().map { entities ->
                entities.map { it.toDomain() }
            }

        override fun observeSpace(spaceId: Long): Flow<PlantSpace?> =
            dao
                .observeSpace(spaceId = spaceId)
                .map { entity -> entity?.toDomain() }

        override suspend fun insertSpace(space: PlantSpace): Long =
            try {
                dao.insertSpace(space.toEntity())
            } catch (e: Exception) {
                Log.e(TAG, "Error inserting space", e)
                throw e
            }

        override suspend fun deleteSpaceAndUnassignPlants(spaceId: Long) =
            try {
                dao.deleteSpaceAndUnassignPlants(spaceId)
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting space $spaceId", e)
                throw e
            }
    }
