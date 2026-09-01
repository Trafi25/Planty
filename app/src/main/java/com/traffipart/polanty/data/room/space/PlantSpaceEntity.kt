package com.traffipart.polanty.data.room.space

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plant_spaces")
data class PlantSpaceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: String,
)
