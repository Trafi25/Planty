package com.traffipart.polanty.presentation.garden

sealed interface GardenAction {
    data class AddCustomSpace(
        val name: String,
    ) : GardenAction

    data object ClearAddSpaceError :
        GardenAction
}
