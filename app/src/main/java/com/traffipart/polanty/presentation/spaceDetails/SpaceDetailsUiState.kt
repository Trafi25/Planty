package com.traffipart.polanty.presentation.spaceDetails

import com.traffipart.polanty.domain.model.Plant
import com.traffipart.polanty.domain.model.PlantSpace

data class SpaceDetailsUiState(
    val space: PlantSpace? = null,
    val plants: List<Plant> = emptyList(),
    val isLoading: Boolean = true,
)
