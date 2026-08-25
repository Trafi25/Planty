package com.traffipart.polanty.data.plant.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlantNetSpeciesDto(
    val scientificName: String,
    val scientificNameWithoutAuthor: String,
    val scientificNameAuthorship: String?,
    val commonNames: List<String> = emptyList(),
    val genus: PlantNetTaxonDto?,
    val family: PlantNetTaxonDto?,
)