package com.traffipart.polanty.domain.usecase.plant

import com.traffipart.polanty.domain.model.Plant
import com.traffipart.polanty.domain.repository.PlantRepository
import com.traffipart.polanty.domain.storage.PlantImageStorage
import java.util.concurrent.CancellationException
import javax.inject.Inject

/**
 * Use case to delete a plant from the garden.
 *
 * This use case removes the plant from the repository and also attempts to delete its associated image
 * from local storage. If image deletion fails, it is silently ignored as the plant record is already gone.
 *
 * @property repository The repository to delete the plant from.
 * @property imageStorage The storage service to handle plant image deletion.
 */
class DeletePlantUseCase
    @Inject
    constructor(
        private val repository: PlantRepository,
        private val imageStorage: PlantImageStorage,
    ) {
        /**
         * Deletes the specified [plant] and its associated resources.
         *
         * @param plant The plant to be deleted.
         */
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
