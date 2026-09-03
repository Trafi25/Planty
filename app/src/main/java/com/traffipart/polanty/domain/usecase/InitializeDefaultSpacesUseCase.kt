package com.traffipart.polanty.domain.usecase

import com.traffipart.polanty.domain.model.PlantSpace
import com.traffipart.polanty.domain.model.PlantSpaceType
import com.traffipart.polanty.domain.repository.PlantSpaceRepository
import com.traffipart.polanty.domain.repository.SpaceInitializationRepository
import jakarta.inject.Inject

class InitializeDefaultSpacesUseCase
    @Inject
    constructor(
        private val plantSpaceRepository: PlantSpaceRepository,
        private val initializationRepository: SpaceInitializationRepository,
    ) {
        suspend operator fun invoke() {
            if (initializationRepository.isInitialized()) return

            plantSpaceRepository.insertSpace(
                PlantSpace(id = 0, name = "Bedroom", type = PlantSpaceType.Bedroom),
            )

            initializationRepository.markInitialized()
        }
    }
