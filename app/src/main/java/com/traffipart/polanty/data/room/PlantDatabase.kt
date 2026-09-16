package com.traffipart.polanty.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.traffipart.polanty.data.room.care.CareTaskDao
import com.traffipart.polanty.data.room.care.CareTaskEntity
import com.traffipart.polanty.data.room.knowledge.PlantKnowledgeDao
import com.traffipart.polanty.data.room.knowledge.PlantKnowledgeEntity
import com.traffipart.polanty.data.room.plant.PlantDao
import com.traffipart.polanty.data.room.plant.PlantEntity
import com.traffipart.polanty.data.room.space.PlantSpaceDao
import com.traffipart.polanty.data.room.space.PlantSpaceEntity

/**
 * Main database definition for the Polanty application managing local botanical data and care tasks.
 *
 * This database registers the following key data models:
 * - [PlantEntity]: Basic records for individual plants.
 * - [PlantSpaceEntity]: Physical or logical spaces grouping multiple plants together.
 * - [PlantKnowledgeEntity]: Cached comprehensive botanical profiles retrieved from remote APIs or AI fallbacks.
 * - [CareTaskEntity]: Logged or upcoming care events tied to specific plants via a cascade-on-delete Foreign Key relationship.
 */
@Database(
    entities = [
        PlantEntity::class,
        PlantSpaceEntity::class,
        PlantKnowledgeEntity::class,
        CareTaskEntity::class,
    ],
    version = 4,
    exportSchema = false,
)
abstract class PlantDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao

    abstract fun plantSpaceDao(): PlantSpaceDao

    abstract fun plantKnowledgeDao(): PlantKnowledgeDao

    /**
     * Provides the data access object for performing care task database operations.
     */
    abstract fun careTaskDao(): CareTaskDao
}
