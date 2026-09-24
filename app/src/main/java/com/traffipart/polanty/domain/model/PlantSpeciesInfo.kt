package com.traffipart.polanty.domain.model

/**
 * Descriptive botanical information for a plant species.
 *
 * @property scientificName Accepted scientific name.
 * @property commonName Primary common name, if available.
 * @property description Detailed description of the plant.
 * @property origin Geographic origin or native range.
 * @property toxicity Toxicity risk levels for pets and humans.
 * @property typicalHeightCmMin Minimum typical indoor/outdoor height in centimeters.
 * @property typicalHeightCmMax Maximum typical indoor/outdoor height in centimeters.
 */
data class PlantSpeciesInfo(
    val scientificName: String,
    val commonName: String?,
    val description: String,
    val origin: String?,
    val toxicity: PlantToxicity,
    val typicalHeightCmMin: Int?,
    val typicalHeightCmMax: Int?,
)

/**
 * Toxicity classification for pets and humans.
 *
 * @property pets Risk level for household pets.
 * @property humans Risk level for humans.
 * @property notes Supplemental safety notes or specific toxic compounds.
 */
data class PlantToxicity(
    val pets: ToxicityLevel,
    val humans: ToxicityLevel,
    val notes: String?,
)

/**
 * Level of toxicity severity.
 */
enum class ToxicityLevel {
    NonToxic,
    Mild,
    Toxic,
    Unknown,
}
