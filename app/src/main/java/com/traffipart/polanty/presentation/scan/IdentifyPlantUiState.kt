package com.traffipart.polanty.presentation.scan

import com.traffipart.polanty.core.common.DataError
import com.traffipart.polanty.domain.model.PlantIdentification

/**
 * UI state for the Plant Identification screen.
 *
 * @property isLoading Whether the identification process is currently running.
 * @property identification The result of the identification, if successful.
 * @property error The error that occurred during identification, if any.
 */
data class IdentifyPlantUiState(
    val isLoading: Boolean = false,
    val identification: PlantIdentification? = null,
    val error: DataError? = null,
)
