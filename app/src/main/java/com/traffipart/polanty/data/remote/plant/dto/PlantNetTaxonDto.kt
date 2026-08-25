package com.traffipart.polanty.data.remote.plant.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlantNetTaxonDto(
    val scientificName: String?,
    val scientificNameWithoutAuthor: String?,
)
