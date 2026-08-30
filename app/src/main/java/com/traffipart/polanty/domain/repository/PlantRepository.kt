package com.traffipart.polanty.domain.repository

import com.traffipart.polanty.domain.model.Plant
import kotlinx.coroutines.flow.Flow

interface PlantRepository {
    fun observePlants(): Flow<List<Plant>>

    suspend fun savePlant(plant: Plant): Long

    suspend fun deletePlant(plant: Plant)
}
