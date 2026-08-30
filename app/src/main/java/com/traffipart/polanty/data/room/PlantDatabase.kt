package com.traffipart.polanty.data.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PlantEntity::class], version = 1, exportSchema = false)
abstract class PlantDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao
}
