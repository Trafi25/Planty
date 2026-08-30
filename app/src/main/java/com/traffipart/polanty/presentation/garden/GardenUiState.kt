package com.traffipart.polanty.presentation.garden

import com.traffipart.polanty.domain.model.Plant

data class GardenUiState(
    val plants: List<Plant> = emptyList(),
    val isLoading: Boolean = true,
)
