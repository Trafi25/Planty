package com.traffipart.polanty.presentation.garden

import com.traffipart.polanty.domain.model.PlantSpaceType

sealed interface GardenAction {
    data class AddSpace(
        val type: PlantSpaceType,
        val customName: String?,
    ) : GardenAction

    data object ClearAddSpaceError :
        GardenAction
}
