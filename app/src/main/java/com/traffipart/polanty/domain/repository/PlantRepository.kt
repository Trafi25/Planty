package com.traffipart.polanty.domain.repository

import com.traffipart.polanty.domain.model.Plant
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing plant records.
 */
interface PlantRepository {
    /**
     * Returns a Flow that emits the list of all plants whenever it changes.
     */
    fun observePlants(): Flow<List<Plant>>

    /**
     * Saves a plant to the repository.
     *
     * @param plant The [Plant] data to save.
     * @return The unique ID of the saved plant.
     */
    suspend fun savePlant(plant: Plant): Long

    /**
     * Deletes a plant from the repository.
     *
     * @param plant The plant to delete.
     */
    suspend fun deletePlant(plant: Plant)

    /**
     * Returns a Flow that emits the plant with the specified [plantId] whenever it changes.
     */
    fun observePlant(plantId: Long): Flow<Plant?>
}
