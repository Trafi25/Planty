package com.traffipart.polanty.domain.usecase.plant

import com.traffipart.polanty.domain.model.Plant
import com.traffipart.polanty.domain.repository.PlantRepository
import com.traffipart.polanty.domain.storage.PlantImageStorage
import jakarta.inject.Inject

/**
 * Use case to save a new plant to the garden.
 *
 * This use case handles saving the plant's image to local storage before saving the plant record
 * in the repository.
 *
 * @property plantRepository The repository to save the plant to.
 * @property plantImageStorage The storage service to handle plant image saving.
 */
class SavePlantUseCase
    @Inject
    constructor(
        private val plantRepository: PlantRepository,
        private val plantImageStorage: PlantImageStorage,
    ) {
        /**
         * Saves a new plant.
         *
         * @param plant The [com.traffipart.polanty.domain.model.Plant] data to save.
         * @param sourceImageUri The optional URI of the source image to be saved locally.
         * @return The unique ID of the newly saved plant.
         */
        suspend operator fun invoke(
            plant: Plant,
            sourceImageUri: String?,
        ): Long {
            val savedImageUri =
                sourceImageUri?.let {
                    plantImageStorage.saveImage(it)
                }
            val plantToSave = plant.copy(imageUri = savedImageUri)
            return plantRepository.savePlant(plantToSave)
        }
    }
