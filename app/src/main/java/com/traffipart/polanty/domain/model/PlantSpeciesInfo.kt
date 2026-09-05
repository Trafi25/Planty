package com.traffipart.polanty.domain.model

data class PlantSpeciesInfo(
    val scientificName: String,
    val commonName: String?,
    val description: String,
    val origin: String?,
    val toxicity: PlantToxicity,
    val typicalHeightCmMin: Int?,
    val typicalHeightCmMax: Int?,
)

data class PlantToxicity(
    val pets: ToxicityLevel,
    val humans: ToxicityLevel,
    val notes: String?,
)

enum class ToxicityLevel {
    NonToxic,
    Mild,
    Toxic,
    Unknown,
}
