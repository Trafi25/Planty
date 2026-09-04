package com.traffipart.polanty.presentation.garden

import com.traffipart.polanty.domain.model.PlantSpaceType

/**
 * Actions that can be performed on the Garden screen.
 */
sealed interface GardenAction {
    /**
     * Action to add a new plant space.
     *
     * @property type The type of the space.
     * @property customName The optional custom name for the space.
     */
    data class AddSpace(
        val type: PlantSpaceType,
        val customName: String?,
    ) : GardenAction

    /**
     * Action to delete an existing plant space.
     *
     * @property spaceId The unique identifier of the space to delete.
     */
    data class DeleteSpace(
        val spaceId: Long,
    ) : GardenAction

    /**
     * Action to clear the error message related to deleting a space.
     */
    data object ClearDeleteSpaceError : GardenAction

    /**
     * Action to clear the error message related to adding a space.
     */
    data object ClearAddSpaceError :
        GardenAction
}
