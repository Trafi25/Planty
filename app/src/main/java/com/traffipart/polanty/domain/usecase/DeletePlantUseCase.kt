package com.traffipart.polanty.domain.usecase

import com.traffipart.polanty.domain.model.Plant
import com.traffipart.polanty.domain.repository.PlantRepository
import com.traffipart.polanty.domain.storage.PlantImageStorage
import java.util.concurrent.CancellationException
import javax.inject.Inject

class DeletePlantUseCase
    @Inject
    constructor(
        private val repository: PlantRepository,
        private val imageStorage: PlantImageStorage,
    ) {
        suspend operator fun invoke(plant: Plant) {
            repository.deletePlant(plant)
            val imageUri = plant.imageUri ?: return

            try {
                imageStorage.deleteImage(imageUri)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                // Plant is already deleted from Room.
            }
        }
    }
