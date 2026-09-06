package com.traffipart.polanty.data.remote.knowledge.dtos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

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

@JsonClass(generateAdapter = true)
data class PerenualDimensionsDto(
    @Json(name = "min_value")
    val minValue: Double?,
    @Json(name = "max_value")
    val maxValue: Double?,
    val unit: String?,
)

@JsonClass(generateAdapter = true)
data class PerenualWateringBenchmarkDto(
    val value: Any?,
    val unit: String?,
)
