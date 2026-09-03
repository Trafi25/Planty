package com.traffipart.polanty.data.room.space

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantSpaceDao {
    @Query("SELECT * FROM plant_spaces ORDER BY name")
    fun observeSpaces(): Flow<List<PlantSpaceEntity>>

    @Insert
    suspend fun insertSpace(space: PlantSpaceEntity): Long

    @Query("UPDATE plants SET spaceId = NULL WHERE spaceId = :spaceId")
    suspend fun unassignPlantsFromSpace(spaceId: Long)

    @Query("DELETE FROM plant_spaces WHERE id = :spaceId")
    suspend fun deleteSpaceById(spaceId: Long)

    @Transaction
    suspend fun deleteSpaceAndUnassignPlants(spaceId: Long) {
        unassignPlantsFromSpace(spaceId)
        deleteSpaceById(spaceId)
    }
}
