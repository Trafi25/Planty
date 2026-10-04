package com.traffipart.polanty.data.repository.care

import com.traffipart.polanty.data.remote.weather.OpenMeteoApi
import com.traffipart.polanty.domain.model.care.CareEnvironment
import com.traffipart.polanty.domain.repository.care.CareEnvironmentRepository
import kotlinx.coroutines.CancellationException
import javax.inject.Inject
import kotlin.math.roundToInt

class CareEnvironmentRepositoryImpl
    @Inject
    constructor(
        private val openMeteoApi: OpenMeteoApi,
    ) : CareEnvironmentRepository {
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
                CareEnvironment(temperatureCelsius, humidityPercent)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                return null
            }
    }
