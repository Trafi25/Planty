package com.traffipart.polanty.domain.usecase.space

import com.traffipart.polanty.domain.model.PlantSpace
import com.traffipart.polanty.domain.model.PlantSpaceType
import com.traffipart.polanty.domain.repository.PlantSpaceRepository
import com.traffipart.polanty.domain.repository.SpaceInitializationRepository
import jakarta.inject.Inject

/**
 * Use case to initialize the application with default plant spaces.
 *
 * This use case checks if the initialization has already been performed. If not, it adds a
 * default "Bedroom" space and marks the application as initialized.
 *
 * @property plantSpaceRepository The repository to insert spaces into.
 * @property initializationRepository The repository to check and update the initialization status.
 */
class InitializeDefaultSpacesUseCase
    @Inject
    constructor(
        private val plantSpaceRepository: PlantSpaceRepository,
        private val initializationRepository: SpaceInitializationRepository,
    ) {
        /**
         * Triggers the initialization of default spaces if they haven't been initialized yet.
         */
        suspend operator fun invoke() {
            if (initializationRepository.isInitialized()) return

            plantSpaceRepository.insertSpace(
                PlantSpace(id = 0, name = "Bedroom", type = PlantSpaceType.Bedroom),
            )

            initializationRepository.markInitialized()
        }
    }
