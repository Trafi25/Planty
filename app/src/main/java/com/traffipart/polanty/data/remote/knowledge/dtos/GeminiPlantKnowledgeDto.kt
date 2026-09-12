package com.traffipart.polanty.data.remote.knowledge.dtos

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeminiPlantKnowledgeDto(
    val scientificName: String,
    val commonName: String,
    val description: String,
    val origin: String,
    val petToxicity: String,
    val humanToxicity: String,
    val toxicityNotes: String,
    val typicalHeightCmMin: Int,
    val typicalHeightCmMax: Int,
    val wateringDaysMin: Int,
    val wateringDaysMax: Int,
    val wateringInstruction: String,
    val lightRequirement: String,
    val humidityMinPercent: Int,
    val humidityMaxPercent: Int,
    val temperatureMinCelsius: Double,
    val temperatureMaxCelsius: Double,
    val fertilizing: String,
)
