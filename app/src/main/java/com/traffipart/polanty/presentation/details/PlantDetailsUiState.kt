package com.traffipart.polanty.presentation.details

import com.traffipart.polanty.domain.model.Plant

/**
 * UI state for the Plant Details screen.
 *
 * @property plant The plant being displayed.
 * @property isLoading Whether the plant data is currently being loaded.
 * @property isDeleting Whether the plant is currently being deleted.
 * @property isDeleted Whether the plant has been successfully deleted.
 * @property errorMessage An optional error message if an operation failed.
 * @property knowledgeState The current state of botanical knowledge retrieval.
 */
data class PlantDetailsUiState(
    val plant: Plant? = null,
    val isLoading: Boolean = true,
    val isDeleting: Boolean = false,
    val isDeleted: Boolean = false,
    val errorMessage: String? = null,
    val knowledgeState: PlantKnowledgeUiState = PlantKnowledgeUiState.Idle,
)
