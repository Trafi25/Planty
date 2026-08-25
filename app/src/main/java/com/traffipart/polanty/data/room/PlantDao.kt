package com.traffipart.polanty.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {
    @Query("SELECT * FROM plants")
    fun observePlants(): Flow<List<PlantEntity>>

    @Insert
    suspend fun insertPlant(plant: PlantEntity): Long

    @Delete
    suspend fun deletePlant(plant: PlantEntity)
}
