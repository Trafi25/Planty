package com.traffipart.polanty.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.traffipart.polanty.data.room.plant.PlantDao
import com.traffipart.polanty.data.room.plant.PlantEntity
import com.traffipart.polanty.data.room.space.PlantSpaceDao
import com.traffipart.polanty.data.room.space.PlantSpaceEntity

@Database(entities = [PlantEntity::class, PlantSpaceEntity::class], version = 2, exportSchema = false)
abstract class PlantDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao

    abstract fun PlantSpaceDao(): PlantSpaceDao
}
