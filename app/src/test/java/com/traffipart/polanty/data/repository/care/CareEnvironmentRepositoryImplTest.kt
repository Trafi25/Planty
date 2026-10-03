package com.traffipart.polanty.data.repository.care

import com.google.common.truth.Truth.assertThat
import com.traffipart.polanty.data.remote.weather.OpenMeteoApi
import com.traffipart.polanty.data.remote.weather.dto.CurrentWeatherDto
import com.traffipart.polanty.data.remote.weather.dto.OpenMeteoResponseDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.IOException

class CareEnvironmentRepositoryImplTest {
    private val api =
        mockk<OpenMeteoApi>()

    private val repository =
        CareEnvironmentRepositoryImpl(
            openMeteoApi = api,
        )

    @Test
    fun returnsCurrentEnvironment() =
        runTest {
            coEvery {
                api.getCurrentWeather(
                    latitude = any(),
                    longitude = any(),
                )
            } returns
                OpenMeteoResponseDto(
                    current =
                        CurrentWeatherDto(
                            temperatureCelsius =
                            31.5,
                            humidityPercent =
                            42.0,
                        ),
                )

            val result =
                repository
                    .getCurrentEnvironment(
                        latitude = 49.45,
                        longitude = 11.08,
                    )

            assertThat(
                result?.temperatureCelsius,
            ).isEqualTo(31.5)

            assertThat(
                result?.humidityPercent,
            ).isEqualTo(42)
        }

    @Test
    fun returnsNullWhenWeatherRequestFails() =
        runTest {
            coEvery {
                api.getCurrentWeather(
                    latitude = any(),
                    longitude = any(),
                )
            } throws IOException()

            val result =
                repository
                    .getCurrentEnvironment(
                        latitude = 49.45,
                        longitude = 11.08,
                    )

            assertThat(result).isNull()
        }
}
