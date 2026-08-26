package com.traffipart.polanty.core.model

data class PlantIdentification(
    val bestMatch: String?,
    val candidates: List<PlantCandidate>,
)

data class PlantCandidate(
    val scientificName: String,
    val commonName: String?,
    val confidence: Double,
)
