package com.traffipart.polanty.data.plant.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlantNetResultDto(
    val score: Double,
    val species: PlantNetSpeciesDto,
)