package com.traffipart.polanty.presentation.spaceDetails

import com.traffipart.polanty.domain.model.plant.Plant
import com.traffipart.polanty.domain.model.space.PlantSpace

data class SpaceDetailsUiState(
    val space: PlantSpace? = null,
    val plants: List<Plant> = emptyList(),
    val isLoading: Boolean = true,
)
