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
    @Json(name = "scientific_name")
    val scientificNames: List<String>? = null,
    @Json(name = "common_name")
    val commonName: String? = null,
    val description: String? = null,
    val origin: List<String>? = null,
    val watering: String? = null,
    val sunlight: List<String>? = null,
    @Json(name = "watering_general_benchmark")
    val wateringBenchmark: PerenualWateringBenchmarkDto? = null,
    val dimensions: PerenualDimensionsDto? = null,
    @Json(name = "poisonous_to_pets")
    val poisonousToPets: Boolean? = null,
    @Json(name = "poisonous_to_humans")
    val poisonousToHumans: Boolean? = null,
)

/**
 * Physical dimensions of the plant (height, spread).
 */
@JsonClass(generateAdapter = true)
data class PerenualDimensionsDto(
    val type: String? = null,
    @Json(name = "min_value")
    val minValue: Double? = null,
    @Json(name = "max_value")
    val maxValue: Double? = null,
    val unit: String? = null,
)

/**
 * Technical benchmark data for plant watering frequency.
 */
@JsonClass(generateAdapter = true)
data class PerenualWateringBenchmarkDto(
    val value: Any? = null,
    val unit: String? = null,
)
