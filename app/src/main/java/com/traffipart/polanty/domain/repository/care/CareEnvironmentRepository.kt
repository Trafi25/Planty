package com.traffipart.polanty.domain.repository.care

import com.traffipart.polanty.domain.model.care.CareEnvironment

/**
 * Repository interface for fetching current outdoor environmental conditions for a location.
 */
interface CareEnvironmentRepository {
    /**
     * Fetches current outdoor weather environmental data for the given geographic coordinates.
     *
     * @param latitude Target location latitude.
     * @param longitude Target location longitude.
     * @return The current [CareEnvironment] if weather data is available, or `null` on failure.
     */
    suspend fun getCurrentEnvironment(
        latitude: Double,
        longitude: Double,
    ): CareEnvironment?
}
