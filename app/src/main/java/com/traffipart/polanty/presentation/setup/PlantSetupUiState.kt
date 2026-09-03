package com.traffipart.polanty.presentation.setup

import com.traffipart.polanty.domain.model.PlantCandidate
import com.traffipart.polanty.domain.model.PlantSpace

/**
 * UI state for the Plant Setup screen.
 *
 * @property candidate The plant candidate being set up.
 * @property nickname The nickname given to the plant.
 * @property spaceId The ID of the space where the plant will be placed.
 * @property spaces The list of available spaces to choose from.
 * @property imageUri The URI of the image associated with the plant.
 * @property isSaving Whether the plant is currently being saved.
 * @property savedPlantId The ID of the plant after it has been successfully saved.
 * @property saveError Whether an error occurred during the saving process.
 */
data class PlantSetupUiState(
    val candidate: PlantCandidate? = null,
    val nickname: String = "",
    val spaceId: Long? = null,
    val spaces: List<PlantSpace> = emptyList(),
    val imageUri: String? = null,
    val isSaving: Boolean = false,
    val savedPlantId: Long? = null,
    val saveError: Boolean = false,
)
