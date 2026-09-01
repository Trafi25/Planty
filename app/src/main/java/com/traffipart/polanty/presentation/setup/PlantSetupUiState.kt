package com.traffipart.polanty.presentation.setup

import com.traffipart.polanty.domain.model.PlantCandidate
import com.traffipart.polanty.domain.model.PlantSpace

data class PlantSetupUiState(
    val candidate: PlantCandidate? = null,
    val nickname: String = "",
    val spaceId: Long? = null,
    val spaces: List<PlantSpace> = emptyList(),
    val imageUri: String? = null,
    val isSaving: Boolean = false,
    val savedPlantId: Long? = null,
    val saveError: Boolean = false,
)
