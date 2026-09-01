package com.traffipart.polanty.domain.usecase

import com.traffipart.polanty.domain.model.Plant
import com.traffipart.polanty.domain.repository.PlantRepository
import com.traffipart.polanty.domain.storage.PlantImageStorage
import jakarta.inject.Inject

class SavePlantUseCase
    @Inject
    constructor(
        private val plantRepository: PlantRepository,
        private val plantImageStorage: PlantImageStorage,
    ) {
        suspend operator fun invoke(
            plant: Plant,
            sourceImageUri: String,
        ): Long {
            val sourceImageUri =
                sourceImageUri.let {
                    plantImageStorage.saveImage(it)
                }
            val plantToSave = plant.copy(imageUri = sourceImageUri)
            return plantRepository.savePlant(plantToSave)
        }
    }
