package com.traffipart.polanty.data.remote.taxonomy.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GbifMatchResponseDto (
    val usage: GbifNameUsageDto? = null,
    val acceptedUsage: GbifNameUsageDto? = null,
    val taxonomicStatus: String? = null,
    val issues: List<String>? = null,
)

@JsonClass(generateAdapter = true)
data class GbifNameUsageDto(
    val name: String? = null,
    val canonicalName: String? = null,
    val genericName: String? = null,
    val specificEpithet: String? = null,
)