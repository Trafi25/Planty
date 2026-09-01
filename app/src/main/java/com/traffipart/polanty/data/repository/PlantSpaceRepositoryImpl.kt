package com.traffipart.polanty.data.repository

import com.traffipart.polanty.data.mapper.toDomain
import com.traffipart.polanty.data.mapper.toEntity
import com.traffipart.polanty.data.room.space.PlantSpaceDao
import com.traffipart.polanty.domain.model.PlantSpace
import com.traffipart.polanty.domain.repository.PlantSpaceRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlantSpaceRepositoryImpl
    @Inject
    constructor(
        private val dao: PlantSpaceDao,
    ) : PlantSpaceRepository {
        override fun observeSpaces(): Flow<List<PlantSpace>> =
            dao.observeSpaces().map { entities ->
                entities.map { it.toDomain() }
            }

        override suspend fun insertSpace(space: PlantSpace): Long = dao.insertSpace(space.toEntity())

        override suspend fun deleteSpace(space: PlantSpace) = dao.deleteSpace(space.toEntity())
    }
