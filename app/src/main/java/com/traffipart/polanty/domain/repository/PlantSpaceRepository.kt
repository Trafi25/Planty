package com.traffipart.polanty.domain.repository

import com.traffipart.polanty.domain.model.PlantSpace
import kotlinx.coroutines.flow.Flow

interface PlantSpaceRepository {
    fun observeSpaces(): Flow<List<PlantSpace>>

    suspend fun insertSpace(space: PlantSpace): Long

    suspend fun deleteSpace(space: PlantSpace)
}
