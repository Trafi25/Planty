package com.traffipart.polanty.presentation.scan

import com.traffipart.polanty.domain.model.PlantImage

/**
 * Actions that can be performed on the Plant Identification screen.
 */
sealed interface IdentifyPlantAction {
    /**
     * Action to trigger the identification of a plant from an image.
     *
     * @property image The image to identify.
     */
    data class IdentifyPlant(
        val image: PlantImage,
    ) : IdentifyPlantAction

    /**
     * Action to clear any current error message.
     */
    data object ClearError : IdentifyPlantAction

    /**
     * Action to reset the screen to its initial state.
     */
    data object Reset : IdentifyPlantAction
}
