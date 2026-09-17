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

    abstract fun careTaskDao(): CareTaskDao
}
