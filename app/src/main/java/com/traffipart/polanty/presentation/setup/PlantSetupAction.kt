package com.traffipart.polanty.presentation.setup

import com.traffipart.polanty.domain.model.PlantCandidate

sealed interface PlantSetupAction {
    data class Initialize(
        val candidate: PlantCandidate,
        val imageUri: String? = null,
    ) : PlantSetupAction

    data class NicknameChanged(
        val nickname: String,
    ) : PlantSetupAction

    data class SpaceIdSelected(
        val spaceId: Long?,
    ) : PlantSetupAction

    data object SavePlant : PlantSetupAction
}
