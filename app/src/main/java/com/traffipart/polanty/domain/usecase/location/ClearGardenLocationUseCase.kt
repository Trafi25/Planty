package com.traffipart.polanty.domain.usecase.location

import com.traffipart.polanty.domain.repository.location.GardenLocationRepository
import javax.inject.Inject

/**
 * Use case to clear the saved garden location coordinates.
 *
 * @property repository Repository for managing saved garden location.
 */
class ClearGardenLocationUseCase
    @Inject
    constructor(
        private val repository: GardenLocationRepository,
    ) {
        /**
         * Clears the saved garden location coordinates.
         */
        suspend operator fun invoke() {
            repository.clearLocation()
        }
    }
