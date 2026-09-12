package com.traffipart.polanty.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class PlantIdentification(
    val bestMatch: String?,
    val candidates: List<PlantCandidate>,
)

@Parcelize
data class PlantCandidate(
    val scientificName: String,
    val commonName: String?,
    val confidence: Double,
) : Parcelable
