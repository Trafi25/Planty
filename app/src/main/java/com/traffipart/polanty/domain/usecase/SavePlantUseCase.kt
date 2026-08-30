package com.traffipart.polanty.domain.usecase

import com.traffipart.polanty.domain.model.Plant
import com.traffipart.polanty.domain.repository.PlantRepository
import jakarta.inject.Inject

class SavePlantUseCase
    @Inject
    constructor(
        private val plantRepository: PlantRepository,
    ) {
        suspend operator fun invoke(plant: Plant): Long = plantRepository.savePlant(plant)
    }
