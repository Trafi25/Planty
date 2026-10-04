package com.traffipart.polanty.data.repository.care

import com.traffipart.polanty.data.remote.weather.OpenMeteoApi
import com.traffipart.polanty.domain.model.care.CareEnvironment
import com.traffipart.polanty.domain.model.care.CareEnvironmentSource
import com.traffipart.polanty.domain.repository.care.CareEnvironmentRepository
import kotlinx.coroutines.CancellationException
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * Implementation of [CareEnvironmentRepository] backed by the Open-Meteo REST API.
 *
 * @property openMeteoApi Retrofit service interface for fetching live current weather data.
 */
class CareEnvironmentRepositoryImpl
    @Inject
    constructor(
        private val openMeteoApi: OpenMeteoApi,
    ) : CareEnvironmentRepository {
        /**
         * Fetches current outdoor weather data for specified coordinates.
         *
         * @param latitude Location latitude.
         * @param longitude Location longitude.
         * @return Current [CareEnvironment] with source set to [CareEnvironmentSource.OutdoorWeather], or `null` on network or parsing failure.
         */
        override suspend fun getCurrentEnvironment(
            latitude: Double,
            longitude: Double,
        ): CareEnvironment? =
            try {
                val current = openMeteoApi.getCurrentWeather(latitude, longitude).current ?: return null
                val temperatureCelsius = current.temperatureCelsius
                val humidityPercent = current.humidityPercent?.roundToInt().takeIf { it in 0..100 }

                if (
                    temperatureCelsius == null && humidityPercent == null
                ) {
                    return null
                }
                CareEnvironment(
                    temperatureCelsius = temperatureCelsius,
                    humidityPercent = humidityPercent,
                    source = CareEnvironmentSource.OutdoorWeather,
                )
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                return null
            }
    }
