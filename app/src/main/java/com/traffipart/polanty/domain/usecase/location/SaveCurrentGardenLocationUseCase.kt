package com.traffipart.polanty.domain.usecase.location

import com.traffipart.polanty.domain.location.DeviceLocationProvider
import com.traffipart.polanty.domain.repository.location.GardenLocationRepository
import javax.inject.Inject

/**
 * Use case to query current device location coordinates and persist them as the user's garden location.
 *
 * @property deviceLocationProvider Provider for retrieving hardware location coordinates.
 * @property gardenLocationRepository Repository for saving garden location coordinates.
 */
class SaveCurrentGardenLocationUseCase
    @Inject
    constructor(
        private val deviceLocationProvider: DeviceLocationProvider,
        private val gardenLocationRepository: GardenLocationRepository,
    ) {
        /**
         * Queries the current device location and persists it if available.
         *
         * @return `true` if current location was acquired and saved, `false` otherwise.
         */
        suspend operator fun invoke(): Boolean {
            val location = deviceLocationProvider.getCurrentLocation() ?: return false
            gardenLocationRepository.saveLocation(location)
            return true
        }
    }
