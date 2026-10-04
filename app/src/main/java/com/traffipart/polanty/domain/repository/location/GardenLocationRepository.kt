package com.traffipart.polanty.domain.repository.location

import com.traffipart.polanty.domain.location.GeoCoordinates
import kotlinx.coroutines.flow.Flow

/**
 * Repository for observing and persisting saved garden location coordinates.
 */
interface GardenLocationRepository {
    /**
     * Returns a reactive stream emitting the current saved garden location coordinates, or `null` if unconfigured.
     */
    fun observeLocation(): Flow<GeoCoordinates?>

    /**
     * Persists new garden location coordinates.
     *
     * @param coordinates The [GeoCoordinates] to save.
     */
    suspend fun saveLocation(coordinates: GeoCoordinates)

    /**
     * Clears any saved garden location coordinates.
     */
    suspend fun clearLocation()
}
