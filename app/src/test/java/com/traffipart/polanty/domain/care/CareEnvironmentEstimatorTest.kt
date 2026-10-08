package com.traffipart.polanty.domain.care

import com.google.common.truth.Truth.assertThat
import com.traffipart.polanty.domain.model.care.CareEnvironment
import com.traffipart.polanty.domain.model.care.CareEnvironmentSource
import com.traffipart.polanty.domain.model.space.ClimateExposure
import org.junit.Test

class CareEnvironmentEstimatorTest {
    private val estimator = CareEnvironmentEstimator()

    @Test
    fun directExposureUsesOutdoorEnvironmentUnchanged() {
        val outdoor =
            CareEnvironment(
                temperatureCelsius = 35.0,
                humidityPercent = 25,
                source =
                    CareEnvironmentSource
                        .OutdoorWeather,
            )

        val result =
            estimator.estimate(
                outdoorEnvironment = outdoor,
                exposure =
                    ClimateExposure.Direct,
            )

        assertThat(result)
            .isEqualTo(outdoor)
    }

    @Test
    fun indirectExposureReducesOutdoorTemperatureInfluence() {
        val outdoor =
            CareEnvironment(
                temperatureCelsius = 40.0,
                humidityPercent = 20,
                source =
                    CareEnvironmentSource
                        .OutdoorWeather,
            )

        val result =
            estimator.estimate(
                outdoorEnvironment = outdoor,
                exposure =
                    ClimateExposure.Indirect,
            )

        assertThat(
            result.temperatureCelsius,
        ).isLessThan(40.0)

        assertThat(
            result.temperatureCelsius,
        ).isGreaterThan(21.0)
    }

    @Test
    fun partialExposureHasMoreWeatherInfluenceThanIndirect() {
        val outdoor =
            CareEnvironment(
                temperatureCelsius = 40.0,
                humidityPercent = 20,
                source =
                    CareEnvironmentSource
                        .OutdoorWeather,
            )

        val partial =
            estimator.estimate(
                outdoor,
                ClimateExposure.Partial,
            )

        val indirect =
            estimator.estimate(
                outdoor,
                ClimateExposure.Indirect,
            )

        assertThat(
            partial.temperatureCelsius,
        ).isGreaterThan(
            indirect.temperatureCelsius,
        )
    }

    @Test
    fun indirectExtremeHeatIsClamped() {
        val outdoor =
            CareEnvironment(
                temperatureCelsius = 100.0,
                humidityPercent = 10,
                source =
                    CareEnvironmentSource
                        .OutdoorWeather,
            )

        val result =
            estimator.estimate(
                outdoor,
                ClimateExposure.Indirect,
            )

        assertThat(
            result.temperatureCelsius,
        ).isAtMost(32.0)
    }
}
