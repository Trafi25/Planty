package com.traffipart.polanty.domain.usecase.space

import com.traffipart.polanty.domain.model.PlantSpace
import com.traffipart.polanty.domain.model.PlantSpaceType
import com.traffipart.polanty.domain.repository.plant.PlantSpaceRepository
import com.traffipart.polanty.domain.repository.plant.SpaceInitializationRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.first

/**
 * Use case to initialize the application with default plant spaces.
 *
 * This use case populates standard room variants (Living room, Bedroom, Kitchen, Balcony, Bathroom, Office)
 * if they are not already present in the database.
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
            val existingSpaces = plantSpaceRepository.observeSpaces().first()
            val existingTypes = existingSpaces.map { it.type }.toSet()

            val defaultTypes =
                listOf(
                    PlantSpaceType.LivingRoom,
                    PlantSpaceType.Bedroom,
                    PlantSpaceType.Kitchen,
                    PlantSpaceType.Balcony,
                    PlantSpaceType.Bathroom,
                    PlantSpaceType.Office,
                )

            for (type in defaultTypes) {
                if (type !in existingTypes) {
                    plantSpaceRepository.insertSpace(
                        PlantSpace(id = 0, name = type.displayName, type = type),
                    )
                }
            }

            initializationRepository.markInitialized()
        }
    }
