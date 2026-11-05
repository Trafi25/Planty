package com.traffipart.polanty.domain.model.health

/**
 * A possible health issue suggested by visible plant symptoms.
 *
 * @property category Broad category of the suspected issue.
 * @property title Short name of the finding.
 * @property description Visible evidence and explanation of the suspected issue.
 * @property confidence Optional provider confidence from 0.0 to 1.0; null when unavailable.
 */
data class PlantHealthFinding(
    val category: PlantHealthIssueCategory,
    val title: String,
    val description: String,
    val confidence: Double? = null,
)
