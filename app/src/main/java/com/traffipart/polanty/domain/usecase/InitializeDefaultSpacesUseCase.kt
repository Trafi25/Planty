package com.traffipart.polanty.domain.usecase

import com.traffipart.polanty.domain.model.PlantSpace
import com.traffipart.polanty.domain.model.PlantSpaceType
import com.traffipart.polanty.domain.repository.PlantSpaceRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.first

class InitializeDefaultSpacesUseCase
    @Inject
    constructor(
        private val plantSpaceRepository: PlantSpaceRepository,
    ) {
        suspend operator fun invoke() {
            val existingSpaces = plantSpaceRepository.observeSpaces().first()

            if (existingSpaces.isNotEmpty()) return

            val defaultSpaces =
                listOf(
                    PlantSpace(id = 0, name = "Living Room", type = PlantSpaceType.LivingRoom),
                    PlantSpace(id = 0, name = "Kitchen", type = PlantSpaceType.Kitchen),
                    PlantSpace(id = 0, name = "Bedroom", type = PlantSpaceType.Bedroom),
                    PlantSpace(id = 0, name = "Balcony", type = PlantSpaceType.Balcony),
                    PlantSpace(id = 0, name = "Office", type = PlantSpaceType.Office),
                    PlantSpace(id = 0, name = "Backyard", type = PlantSpaceType.Backyard),
                )

            defaultSpaces.forEach { space ->
                plantSpaceRepository.insertSpace(space)
            }
        }
    }
