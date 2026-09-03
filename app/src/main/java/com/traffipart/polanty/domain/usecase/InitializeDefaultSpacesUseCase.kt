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

            plantSpaceRepository.insertSpace(
                PlantSpace(id = 0, name = "Bedroom", type = PlantSpaceType.Bedroom),
            )
        }
    }
