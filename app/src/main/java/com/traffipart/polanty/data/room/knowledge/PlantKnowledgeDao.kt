package com.traffipart.polanty.data.room.knowledge

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlantKnowledgeDao {

    @Query("SELECT * FROM plant_knowledge " +
            "WHERE lookupScientificName = :lookupName LIMIT 1")
    suspend fun getByScientificName(lookupName: String): PlantKnowledgeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PlantKnowledgeEntity)

}