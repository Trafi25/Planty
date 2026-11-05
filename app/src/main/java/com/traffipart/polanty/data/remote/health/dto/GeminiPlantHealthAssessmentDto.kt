package com.traffipart.polanty.data.remote.health.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeminiPlantHealthAssessmentDto(
    val plantDetected: Boolean,
    val status: String,
    val summary: String,
    val findings: List<GeminiPlantHealthFindingDto>,
    val careRecommendations: List<String>,
)

@JsonClass(generateAdapter = true)
data class GeminiPlantHealthFindingDto(
    val category: String,
    val title: String,
    val evidence: String,
)
