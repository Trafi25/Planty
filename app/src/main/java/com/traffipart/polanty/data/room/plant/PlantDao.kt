package com.traffipart.polanty.data.room.plant

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {
    @Query("SELECT * FROM plants")
    fun observePlants(): Flow<List<PlantEntity>>

    @Query("SELECT * FROM plants WHERE spaceId = :spaceId")
    fun observePlantsBySpace(spaceId: Long): Flow<List<PlantEntity>>

    @Query("SELECT * FROM plants WHERE id = :plantId LIMIT 1")
    fun observePlant(plantId: Long): Flow<PlantEntity?>

    @Insert
    suspend fun insertPlant(plant: PlantEntity): Long

    @Delete
    suspend fun deletePlant(plant: PlantEntity)
}
