package com.traffipart.polanty.presentation.garden

import com.traffipart.polanty.domain.model.Plant
import com.traffipart.polanty.domain.model.PlantSpace

data class GardenUiState(
    val plants: List<Plant> = emptyList(),
    val spaces: List<PlantSpace> = emptyList(),
    val isLoading: Boolean = true,
    val isAddingSpace: Boolean = false,
    val addSpaceError: String? = null,
)
