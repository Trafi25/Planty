package com.traffipart.polanty.data.room.knowledge

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "plant_knowledge",
)
data class PlantKnowledgeEntity(
    @PrimaryKey
    val lookupScientificName: String,
    val scientificName: String,
    val commonName: String?,
    val description: String,
    val origin: String?,
    val petToxicity: String,
    val humanToxicity: String,
    val toxicityNotes: String?,
    val heightMinCm: Int?,
    val heightMaxCm: Int?,
    val wateringDaysMin: Int?,
    val wateringDaysMax: Int?,
    val wateringInstruction: String?,
    val lightRequirement: String?,
    val humidityMinPercent: Int?,
    val humidityMaxPercent: Int?,
    val temperatureMinCelsius: Double?,
    val temperatureMaxCelsius: Double?,
    val fertilizing: String?,
    val cachedAt: Long,
)
