package com.traffipart.polanty.presentation.garden

import com.traffipart.polanty.domain.model.Plant
import com.traffipart.polanty.domain.model.PlantSpace

/**
 * UI state for the Garden screen.
 *
 * @property plants The list of plants in the garden.
 * @property spaces The list of plant spaces available.
 * @property isLoading Whether the data is still being loaded.
 * @property isAddingSpace Whether a new space is currently being added.
 * @property addSpaceError An optional error message if adding a space failed.
 */
data class GardenUiState(
    val plants: List<Plant> = emptyList(),
    val spaces: List<PlantSpace> = emptyList(),
    val isLoading: Boolean = true,
    val isAddingSpace: Boolean = false,
    val addSpaceError: String? = null,
)
