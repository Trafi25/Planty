package com.traffipart.polanty.presentation.scan

import com.traffipart.polanty.core.common.DataError
import com.traffipart.polanty.domain.model.PlantIdentification

data class IdentifyPlantUiState(
    val isLoading: Boolean = false,
    val identification: PlantIdentification? = null,
    val error: DataError? = null,
)
