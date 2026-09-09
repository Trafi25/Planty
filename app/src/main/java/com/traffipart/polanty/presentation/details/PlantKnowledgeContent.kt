package com.traffipart.polanty.presentation.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.traffipart.polanty.domain.model.LightRequirement
import com.traffipart.polanty.domain.model.PlantKnowledge
import com.traffipart.polanty.domain.model.ToxicityLevel
import com.traffipart.polanty.ui.theme.spacing

/**
 * Composable that displays botanical and care information for a plant based on the current state.
 *
 * @param state The current [PlantKnowledgeUiState].
 * @param onRetry Callback triggered when the user wants to retry a failed request.
 * @param modifier The modifier to be applied to the layout.
 */
@Composable
fun PlantKnowledgeContent(
    state: PlantKnowledgeUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
    ) {
        when (state) {
            PlantKnowledgeUiState.Idle -> Unit
            PlantKnowledgeUiState.Loading -> CircularProgressIndicator()
            PlantKnowledgeUiState.Unavailable -> Text("Care information is not available for this species.")
            is PlantKnowledgeUiState.Error -> {
                Text(state.message)
                TextButton(onClick = onRetry) {
                    Text("Retry")
                }
            }
            is PlantKnowledgeUiState.Available -> {
                CareGuideContent(knowledge = state.plantKnowledge)
                AboutPlantContent(knowledge = state.plantKnowledge)
            }
        }
    }
}

/**
 * Displays the care guide section, including watering and light requirements.
 *
 * @param knowledge The plant knowledge containing the care profile.
 */
@Composable
fun CareGuideContent(knowledge: PlantKnowledge) {
    val careProfile = knowledge.careProfile
    Text(text = "Care Guide", style = MaterialTheme.typography.titleLarge)
    if (careProfile == null) {
        Text("Detailed care information is not available.")
        return
    }
    careProfile.watering?.let { watering ->
        Text(
            text =
                "Check soil every " +
                        "${watering.soilCheckIntervalDaysMin}" +
                        "–" +
                        "${watering.soilCheckIntervalDaysMax}" +
                        " days",
        )

    Text(
        text =
            watering.instruction,
    )
    }
    careProfile.light?.let { light ->
        Text(
            text =
                "Light: ${light.toDisplayText()}",
        )
    }
}

/**
 * Displays botanical information about the plant, such as description, origin, and toxicity.
 *
 * @param knowledge The plant knowledge containing the species info.
 */
@Composable
private fun AboutPlantContent(knowledge: PlantKnowledge) {
    val info = knowledge.speciesInfo
    Text(
        text = "About this plant",
        style =
            MaterialTheme.typography.titleLarge,
    )
    if (info.description.isNotBlank()) {
        Text(
            text = info.description,
        )
    }
    info.origin?.let {
        Text(
            text = "Origin: ${info.origin}",
        )
    }
    formatHeight(
        min = info.typicalHeightCmMin,
        max = info.typicalHeightCmMax,
    )?.let { height ->
        Text(
            text = "Typical height: $height",
        )
    }
    Text(
        text =
            "Pet safety: " +
                info.toxicity.pets
                    .toDisplayText(),
    )

    Text(
        text =
            "Human safety: " +
                info.toxicity.humans
                    .toDisplayText(),
    )
}

private fun formatHeight(
    min: Int?,
    max: Int?,
): String? =
    when {
        min != null && max != null && min != max -> "$min–$max cm"
        min != null -> "$min cm"
        max != null -> "$max cm"
        else -> null
    }

private fun ToxicityLevel.toDisplayText(): String =
    when (this) {
        ToxicityLevel.NonToxic ->
            "Non-toxic"
        ToxicityLevel.Mild ->
            "Mildly toxic"
        ToxicityLevel.Toxic ->
            "Toxic"
        ToxicityLevel.Unknown ->
            "Unknown"
    }

private fun LightRequirement.toDisplayText(): String =
    when (this) {
        LightRequirement.Low ->
            "Low light"

        LightRequirement.MediumIndirect ->
            "Medium indirect light"

        LightRequirement.BrightIndirect ->
            "Bright indirect light"

        LightRequirement.Direct ->
            "Direct light"
    }
