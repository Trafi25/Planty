package com.traffipart.polanty.domain.care

import com.google.common.truth.Truth.assertThat
import com.traffipart.polanty.domain.model.care.CareEnvironment
import com.traffipart.polanty.domain.model.care.CareEnvironmentSource
import com.traffipart.polanty.domain.model.knowledge.HumidityRange
import com.traffipart.polanty.domain.model.knowledge.TemperatureRange
import com.traffipart.polanty.domain.model.knowledge.WateringProfile
import org.junit.jupiter.api.Test

/**
 * Unit tests for [SoilCheckIntervalCalculator], verifying interval calculation behavior under
 * various ambient temperature and humidity environmental conditions.
 */
class SoilCheckIntervalCalculatorTest {
    private val calculator = SoilCheckIntervalCalculator()

    private val watering =
        WateringProfile(
            soilCheckIntervalDaysMin = 7,
            soilCheckIntervalDaysMax = 10,
            instruction = "Check soil first",
        )

    private val humidity =
        HumidityRange(
            minPercent = 40,
            maxPercent = 60,
        )

    private val temperature =
        TemperatureRange(
            minCelsius = 18.0,
            maxCelsius = 26.0,
        )

    /**
     * Verifies that when no environmental data is provided, the calculator defaults to the minimum check interval.
     */
    @Test
    fun noEnvironmentUsesMinimumInterval() {
        val result =
            calculator.calculateDays(
                watering = watering,
                humidityRange = humidity,
                temperatureRange = temperature,
                environment = null,
            )

        assertThat(result)
            .isEqualTo(7)
    }

    /**
     * Verifies that when ambient temperature exceeds the species's maximum preference, the calculator uses the minimum check interval.
     */
    @Test
    fun hotEnvironmentUsesMinimumInterval() {
        val result =
            calculator.calculateDays(
                watering = watering,
                humidityRange = humidity,
                temperatureRange = temperature,
                environment =
                    CareEnvironment(
                        temperatureCelsius = 31.0,
                        humidityPercent = 50,
                        source = CareEnvironmentSource.OutdoorWeather,
                    ),
            )

        assertThat(result)
            .isEqualTo(7)
    }

    /**
     * Verifies that when ambient humidity exceeds the species's maximum preference, the calculator uses the maximum check interval.
     */
    @Test
    fun humidEnvironmentUsesMaximumInterval() {
        val result =
            calculator.calculateDays(
                watering = watering,
                humidityRange = humidity,
                temperatureRange = temperature,
                environment =
                    CareEnvironment(
                        temperatureCelsius = 22.0,
                        humidityPercent = 80,
                        source = CareEnvironmentSource.OutdoorWeather,
                    ),
            )

        assertThat(result)
            .isEqualTo(10)
    }

    /**
     * Verifies that when ambient environmental conditions fall within optimal species bounds, the calculator uses the midpoint interval.
     */
    @Test
    fun normalEnvironmentUsesMiddleInterval() {
        val result =
            calculator.calculateDays(
                watering = watering,
                humidityRange = humidity,
                temperatureRange = temperature,
                environment =
                    CareEnvironment(
                        temperatureCelsius = 22.0,
                        humidityPercent = 50,
                        source = CareEnvironmentSource.OutdoorWeather,
                    ),
            )

        assertThat(result)
            .isEqualTo(8)
    }
}
