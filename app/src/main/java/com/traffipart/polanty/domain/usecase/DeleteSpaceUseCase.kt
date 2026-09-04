package com.traffipart.polanty.domain.usecase

import com.traffipart.polanty.domain.repository.PlantSpaceRepository
import jakarta.inject.Inject

/**
 * Use case for deleting a plant space and unassigning any plants associated with it.
 */
class DeleteSpaceUseCase
    @Inject
    constructor(
        private val repository: PlantSpaceRepository,
    ) {
        /**
         * Deletes a space by its ID.
         *
         * @param spaceId The unique identifier of the space to delete.
         */
        suspend operator fun invoke(spaceId: Long) {
            repository.deleteSpaceAndUnassignPlants(spaceId)
        }
    }
