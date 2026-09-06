package com.traffipart.polanty.data.remote.knowledge.dtos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Detailed botanical and care information for a specific species from the Perenual API.
 *
 * @property id The unique database ID.
 * @property commonName The common name of the plant.
 * @property scientificNames A list of scientific names associated with the species.
 * @property origin The geographic origin of the plant.
 * @property dimensions The physical size dimensions of the plant.
 * @property watering General watering instructions.
 * @property wateringBenchmark Specific watering frequency indicators.
 * @property sunlight Light requirements.
 * @property poisonousToHumans Safety information for human contact.
 * @property poisonousToPets Safety information for pets.
 * @property description A botanical description of the plant.
 */
@JsonClass(generateAdapter = true)
data class PerenualSpeciesDetailsDto(
    val id: Int,
    @Json(name = "common_name")
    val commonName: String?,
    @Json(name = "scientific_name")
    val scientificNames: List<String>,
    val origin: List<String>?,
    val dimensions: PerenualDimensionsDto?,
    val watering: String?,
    @Json(
        name = "watering_general_benchmark",
    )
    val wateringBenchmark: PerenualWateringBenchmarkDto?,
    val sunlight: List<String>?,
    @Json(name = "poisonous_to_humans")
    val poisonousToHumans: Boolean?,
    @Json(name = "poisonous_to_pets")
    val poisonousToPets: Boolean?,
    val description: String?,
)

/**
 * Physical dimensions of the plant (height, spread).
 */
@JsonClass(generateAdapter = true)
data class PerenualDimensionsDto(
    @Json(name = "min_value")
    val minValue: Double?,
    @Json(name = "max_value")
    val maxValue: Double?,
    val unit: String?,
)

/**
 * Technical benchmark data for plant watering frequency.
 */
@JsonClass(generateAdapter = true)
data class PerenualWateringBenchmarkDto(
    val value: Any?,
    val unit: String?,
)
