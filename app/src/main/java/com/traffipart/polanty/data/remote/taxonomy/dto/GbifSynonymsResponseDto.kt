package com.traffipart.polanty.data.remote.taxonomy.dto

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class GbifSynonymsResponseDto(
    val results: List<GbifSynonymDto> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class GbifSynonymDto(
    val canonicalName: String? = null,
    val scientificName: String? = null,
    val rank: String? = null,
)