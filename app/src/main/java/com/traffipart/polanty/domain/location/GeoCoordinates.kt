package com.traffipart.polanty.domain.location

/**
 * Represents geographic coordinates on Earth.
 *
 * @property latitude Latitude coordinate in degrees (-90.0 to 90.0).
 * @property longitude Longitude coordinate in degrees (-180.0 to 180.0).
 */
data class GeoCoordinates(
    val latitude: Double,
    val longitude: Double,
)
