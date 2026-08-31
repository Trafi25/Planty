package com.traffipart.polanty.presentation.details

import com.traffipart.polanty.domain.model.Plant

data class PlantDetailsUiState(
    val plant: Plant? = null,
    val isLoading: Boolean = true,
    val isDeleting: Boolean = false,
    val isDeleted: Boolean = false,
    val errorMessage: String? = null,
)
