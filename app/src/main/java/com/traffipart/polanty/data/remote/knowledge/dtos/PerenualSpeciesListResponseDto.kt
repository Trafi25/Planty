package com.traffipart.polanty.data.remote.knowledge.dtos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PerenualSpeciesListResponseDto(
    val data: List<PerenualSpeciesSummaryDto>,
)

@JsonClass(generateAdapter = true)
data class PerenualSpeciesSummaryDto(
    val id: Int,
    @Json(name = "common_name")
    val commonName: String,
    @Json(name = "scientific_name")
    val scientificNames: List<String>,
)
