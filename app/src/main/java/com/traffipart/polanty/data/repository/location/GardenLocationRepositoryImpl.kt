package com.traffipart.polanty.data.repository.location

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.traffipart.polanty.domain.location.GeoCoordinates
import com.traffipart.polanty.domain.repository.location.GardenLocationRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.gardenLocationDataStore by preferencesDataStore(name = "garden_location")

class GardenLocationRepositoryImpl
    @Inject
    constructor(
        @ApplicationContext
        private val context: Context,
    ) : GardenLocationRepository {
        override fun observeLocation(): Flow<GeoCoordinates?> =
            context.gardenLocationDataStore
                .data
                .map { preferences ->
                    val latitude = preferences[LATITUDE_KEY] ?: return@map null
                    val longitude = preferences[LONGITUDE_KEY] ?: return@map null
                    if (latitude !in -90.0..90.0 && longitude !in -180.0..180.0) return@map null
                    GeoCoordinates(latitude, longitude)
                }

        override suspend fun saveLocation(coordinates: GeoCoordinates) {
            require(coordinates.latitude in -90.0..90.0)
            require(coordinates.longitude in -180.0..180.0)
            context.gardenLocationDataStore.edit { preferences ->
                preferences[LATITUDE_KEY] = coordinates.latitude
                preferences[LONGITUDE_KEY] = coordinates.longitude
            }
        }

        override suspend fun clearLocation() {
            context.gardenLocationDataStore.edit { preferences ->
                preferences.remove(LATITUDE_KEY)
                preferences.remove(LONGITUDE_KEY)
            }
        }

        private companion object {
            val LATITUDE_KEY =
                doublePreferencesKey(
                    "garden_latitude",
                )

            val LONGITUDE_KEY =
                doublePreferencesKey(
                    "garden_longitude",
                )
        }
    }
