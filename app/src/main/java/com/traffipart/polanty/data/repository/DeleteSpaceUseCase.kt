package com.traffipart.polanty.data.repository

import com.traffipart.polanty.domain.repository.PlantSpaceRepository
import jakarta.inject.Inject

class DeleteSpaceUseCase
    @Inject
    constructor(
        private val repository: PlantSpaceRepository,
    ) {
        suspend operator fun invoke(spaceId: Long) {
            repository.deleteSpaceAndUnassignPlants(spaceId)
        }
    }
