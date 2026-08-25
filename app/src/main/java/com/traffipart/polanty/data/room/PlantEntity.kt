package com.traffipart.polanty.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plants")
data class PlantEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val scientificName: String,
    val commonName: String?,
    val nickname: String?,
    val spaceId: Long?,
    val imageUri: String?,
)
