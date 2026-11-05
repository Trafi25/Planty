package com.traffipart.polanty.domain.model.health

/**
 * Image-based assessment of a plant's visible condition, independent of storage or an AI provider.
 * Findings describe possible issues rather than confirmed diagnoses.
 *
 * @property status Overall assessment, including uncertainty when the image is inconclusive.
 * @property summary Short explanation of the visible condition.
 * @property findings Possible issues observed in the image; empty when no issues are identified.
 * @property careRecommendations Suggested next steps for the user to consider.
 */
data class PlantHealthAssessment(
    val status: PlantHealthStatus,
    val summary: String,
    val findings: List<PlantHealthFinding> = emptyList(),
    val careRecommendations: List<String> = emptyList(),
)
