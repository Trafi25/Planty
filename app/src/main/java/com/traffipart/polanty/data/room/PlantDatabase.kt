package com.traffipart.polanty.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.traffipart.polanty.data.room.knowledge.PlantKnowledgeDao
import com.traffipart.polanty.data.room.knowledge.PlantKnowledgeEntity
import com.traffipart.polanty.data.room.plant.PlantDao
import com.traffipart.polanty.data.room.plant.PlantEntity
import com.traffipart.polanty.data.room.space.PlantSpaceDao
import com.traffipart.polanty.data.room.space.PlantSpaceEntity

@Database( entities = [
    PlantEntity::class,
    PlantSpaceEntity::class,
    PlantKnowledgeEntity::class,
], version = 3, exportSchema = false)
abstract class PlantDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao

    abstract fun plantSpaceDao(): PlantSpaceDao

    abstract fun plantKnowledgeDao(): PlantKnowledgeDao
}
val MIGRATION_2_3 =
    object : Migration(2, 3) {
        override fun migrate(
            database: SupportSQLiteDatabase,
        ) {
            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS plant_knowledge (
                    lookupScientificName TEXT NOT NULL,
                    scientificName TEXT NOT NULL,
                    commonName TEXT,
                    description TEXT NOT NULL,
                    origin TEXT,
                    petToxicity TEXT NOT NULL,
                    humanToxicity TEXT NOT NULL,
                    toxicityNotes TEXT,
                    heightMinCm INTEGER,
                    heightMaxCm INTEGER,
                    wateringDaysMin INTEGER,
                    wateringDaysMax INTEGER,
                    wateringInstruction TEXT,
                    lightRequirement TEXT,
                    humidityMinPercent INTEGER,
                    humidityMaxPercent INTEGER,
                    temperatureMinCelsius REAL,
                    temperatureMaxCelsius REAL,
                    fertilizing TEXT,
                    cachedAt INTEGER NOT NULL,
                    PRIMARY KEY(lookupScientificName)
                )
                """.trimIndent(),
            )
        }
    }

