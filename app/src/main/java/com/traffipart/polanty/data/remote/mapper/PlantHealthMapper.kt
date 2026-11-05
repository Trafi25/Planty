package com.traffipart.polanty.data.remote.mapper

import com.traffipart.polanty.core.common.trimToNull
import com.traffipart.polanty.data.remote.health.dto.GeminiPlantHealthAssessmentDto
import com.traffipart.polanty.domain.model.health.PlantHealthAssessment
import com.traffipart.polanty.domain.model.health.PlantHealthFinding
import com.traffipart.polanty.domain.model.health.PlantHealthIssueCategory
import com.traffipart.polanty.domain.model.health.PlantHealthStatus

fun GeminiPlantHealthAssessmentDto.toDomain(): PlantHealthAssessment? {
    val parsedStatus =
        PlantHealthStatus.entries
            .firstOrNull {
                it.name.equals(
                    status,
                    ignoreCase = true,
                )
            }
            ?: return null

    val cleanSummary =
        summary.trimToNull()
            ?: return null

    val mappedFindings =
        findings.mapNotNull { finding ->
            val title = finding.title.trimToNull() ?: return@mapNotNull null
            val description = finding.evidence.trimToNull() ?: return@mapNotNull null

            val category =
                PlantHealthIssueCategory.entries
                    .firstOrNull {
                        it.name.equals(
                            finding.category,
                            ignoreCase = true,
                        )
                    }
                    ?: PlantHealthIssueCategory.Unknown

            PlantHealthFinding(
                category = category,
                title = title,
                description = description,
            )
        }

    val recommendations =
        careRecommendations
            .mapNotNull {
                it.trimToNull()
            }.distinct()
            .take(5)

    return PlantHealthAssessment(
        status = parsedStatus,
        summary = cleanSummary,
        findings = mappedFindings,
        careRecommendations = recommendations,
    )
}
