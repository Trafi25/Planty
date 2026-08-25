package com.traffipart.polanty.data.remote.plant.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlantNetResponseDto(
    val bestMatch: String?,
    val results: List<PlantNetResultDto>,
    val version: String?,
    val remainingIdentificationRequests: Int?,
)
