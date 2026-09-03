package com.traffipart.polanty.domain.repository

import com.traffipart.polanty.domain.model.PlantSpace
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing plant spaces.
 */
interface PlantSpaceRepository {
    /**
     * Returns a Flow that emits the list of all plant spaces whenever it changes.
     */
    fun observeSpaces(): Flow<List<PlantSpace>>

    /**
     * Inserts a new plant space or updates an existing one.
     *
     * @param space The [PlantSpace] to insert.
     * @return The unique ID of the inserted space.
     */
    suspend fun insertSpace(space: PlantSpace): Long

    /**
     * Deletes a plant space and unassigns all plants currently associated with it.
     *
     * @param spaceId The unique ID of the space to delete.
     */
    suspend fun deleteSpaceAndUnassignPlants(spaceId: Long)
}
