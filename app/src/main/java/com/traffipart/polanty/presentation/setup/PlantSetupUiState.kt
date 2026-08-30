package com.traffipart.polanty.presentation.setup

import com.traffipart.polanty.domain.model.PlantCandidate

data class PlantSetupUiState(
    val candidate: PlantCandidate? = null,
    val nickname: String = "",
    val spaceId: Long? = null,
    val imageUri: String? = null,
    val isSaving: Boolean = false,
    val savedPlantId: Long? = null,
    val saveError: Boolean = false,
)
