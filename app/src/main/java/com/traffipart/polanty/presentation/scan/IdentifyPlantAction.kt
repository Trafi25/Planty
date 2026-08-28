package com.traffipart.polanty.presentation.scan

import com.traffipart.polanty.domain.model.PlantImage

sealed interface IdentifyPlantAction {
    data class IdentifyPlant(
        val image: PlantImage,
    ) : IdentifyPlantAction

    data object ClearError : IdentifyPlantAction

    data object Reset : IdentifyPlantAction
}
