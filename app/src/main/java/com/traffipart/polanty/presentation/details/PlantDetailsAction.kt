package com.traffipart.polanty.presentation.details

sealed interface PlantDetailsAction {
    data object DeletePlant : PlantDetailsAction
}
