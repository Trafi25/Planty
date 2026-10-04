package com.traffipart.polanty.domain.location

interface DeviceLocationProvider {
    suspend fun getCurrentLocation(): GeoCoordinates?
}
