package com.traffipart.polanty.domain.usecase.location

import com.traffipart.polanty.domain.location.DeviceLocationProvider
import com.traffipart.polanty.domain.repository.location.GardenLocationRepository
import javax.inject.Inject

class SaveCurrentGardenLocationUseCase @Inject constructor(
    private val deviceLocationProvider: DeviceLocationProvider,
    private val gardenLocationRepository: GardenLocationRepository,
) {

    suspend operator fun invoke(): Boolean {
        val location = deviceLocationProvider.getCurrentLocation() ?: return false
        gardenLocationRepository.saveLocation(location)
        return true
    }
}