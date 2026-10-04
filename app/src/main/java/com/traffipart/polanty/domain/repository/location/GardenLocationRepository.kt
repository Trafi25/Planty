package com.traffipart.polanty.domain.repository.location

import com.traffipart.polanty.domain.location.GeoCoordinates
import kotlinx.coroutines.flow.Flow

interface GardenLocationRepository {
    fun observeLocation(): Flow<GeoCoordinates?>

    suspend fun saveLocation(coordinates: GeoCoordinates)

    suspend fun clearLocation()
}
