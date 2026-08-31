package com.traffipart.polanty.domain.usecase

import com.traffipart.polanty.domain.model.Plant
import com.traffipart.polanty.domain.repository.PlantRepository
import javax.inject.Inject

class DeletePlantUseCase
    @Inject
    constructor(
        private val repository: PlantRepository,
    ) {
        suspend operator fun invoke(plant: Plant) {
            repository.deletePlant(plant)
        }
    }
