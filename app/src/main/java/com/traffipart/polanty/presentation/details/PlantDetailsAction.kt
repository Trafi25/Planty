package com.traffipart.polanty.presentation.details

/**
 * Actions that can be performed on the Plant Details screen.
 */
sealed interface PlantDetailsAction {
    /**
     * Action to delete the current plant.
     */
    data object DeletePlant : PlantDetailsAction
}
