package com.traffipart.polanty.data.room.space

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantSpaceDao {
    @Query("SELECT * FROM plant_spaces ORDER BY name")
    fun observeSpaces(): Flow<List<PlantSpaceEntity>>

    @Insert
    suspend fun insertSpace(space: PlantSpaceEntity): Long

    @Delete
    suspend fun deleteSpace(space: PlantSpaceEntity)
}
