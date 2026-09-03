package com.traffipart.polanty.presentation.setup

import com.traffipart.polanty.domain.model.PlantCandidate

/**
 * Actions that can be performed on the Plant Setup screen.
 */
sealed interface PlantSetupAction {
    /**
     * Initializes the setup process with a plant candidate and an optional image URI.
     *
     * @property candidate The plant candidate to initialize with.
     * @property imageUri The optional image URI to initialize with.
     */
    data class Initialize(
        val candidate: PlantCandidate,
        val imageUri: String? = null,
    ) : PlantSetupAction

    /**
     * Action when the plant nickname is changed.
     *
     * @property nickname The new nickname.
     */
    data class NicknameChanged(
        val nickname: String,
    ) : PlantSetupAction

    /**
     * Action when a space is selected for the plant.
     *
     * @property spaceId The ID of the selected space.
     */
    data class SpaceIdSelected(
        val spaceId: Long?,
    ) : PlantSetupAction

    /**
     * Action to save the plant to the garden.
     */
    data object SavePlant : PlantSetupAction
}
