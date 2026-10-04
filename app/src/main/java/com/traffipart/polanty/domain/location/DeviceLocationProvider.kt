package com.traffipart.polanty.domain.location

/**
 * Interface defining operations for acquiring current hardware device location coordinates.
 */
interface DeviceLocationProvider {
    /**
     * Retrieves the current device location coordinates, if permissions are granted and location is available.
     *
     * @return The current [GeoCoordinates], or `null` if location services are disabled or permissions are missing.
     */
    suspend fun getCurrentLocation(): GeoCoordinates?
}
