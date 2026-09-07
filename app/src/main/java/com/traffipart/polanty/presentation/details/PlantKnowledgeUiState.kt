package com.traffipart.polanty.presentation.details

import com.traffipart.polanty.domain.model.PlantKnowledge

/**
 * Represents the various states of plant knowledge (botanical and care information)
 * on the Plant Details screen.
 */
sealed interface PlantKnowledgeUiState {
    /** Initial state before any request is made. */
    data object Idle : PlantKnowledgeUiState

    /** State indicating that plant knowledge is currently being fetched. */
    data object Loading : PlantKnowledgeUiState

    /**
     * State indicating that plant knowledge was successfully retrieved.
     *
     * @property plantKnowledge The retrieved botanical and care information.
     */
    data class Available(
        val plantKnowledge: PlantKnowledge,
    ) : PlantKnowledgeUiState

    /** State indicating that no botanical information was found for the specific species. */
    data object Unavailable : PlantKnowledgeUiState

    /**
     * State indicating that an error occurred while fetching plant knowledge.
     *
     * @property message A descriptive error message.
     */
    data class Error(
        val message: String,
    ) : PlantKnowledgeUiState
}
