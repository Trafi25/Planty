package com.traffipart.polanty.domain.care

import com.traffipart.polanty.domain.model.care.CareEnvironment
import com.traffipart.polanty.domain.model.care.CareEnvironmentSource
import com.traffipart.polanty.domain.model.space.ClimateExposure
import javax.inject.Inject

/**
 * Estimator that converts outdoor weather conditions into localized microclimate environments
 * based on a plant space's [ClimateExposure].
 *
 * Indoor environments are insulated against extreme outdoor weather swings, shifting temperatures
 * toward a standard indoor baseline (~21°C) and relative humidity toward ~50%.
 */
class CareEnvironmentEstimator
    @Inject
    constructor() {
        /**
         * Estimates environmental conditions for a specific space exposure level.
         *
         * @param outdoorEnvironment The raw outdoor weather environment.
         * @param exposure The [ClimateExposure] of the plant's room or location.
         * @return The estimated [CareEnvironment] adjusted for the given climate exposure.
         */
        fun estimate(
            outdoorEnvironment: CareEnvironment,
            exposure: ClimateExposure,
        ): CareEnvironment =
            when (exposure) {
                ClimateExposure.Direct -> outdoorEnvironment
                ClimateExposure.Partial -> estimatePartial(outdoorEnvironment)
                ClimateExposure.Indirect -> estimateIndoor(outdoorEnvironment)
            }

        /**
         * Dampens outdoor weather fluctuations to model an indoor climate.
         */
        private fun estimateIndoor(outdoorEnvironment: CareEnvironment): CareEnvironment =
            CareEnvironment(
                temperatureCelsius =
                    outdoorEnvironment.temperatureCelsius?.let { temperature ->
                        estimateTowardIndoor(outdoorTemperature = temperature, influence = 0.25)
                    },
                humidityPercent =
                    outdoorEnvironment.humidityPercent?.let { humidity ->
                        estimateHumidityTowardIndoor(outdoorHumidity = humidity, influence = 0.35)
                    },
                source = CareEnvironmentSource.EstimatedIndoor,
            )

        /**
         * Partially dampens outdoor weather fluctuations for semi-sheltered spaces like balconies.
         */
        private fun estimatePartial(outdoor: CareEnvironment): CareEnvironment =
            CareEnvironment(
                temperatureCelsius =
                    outdoor.temperatureCelsius?.let { temperature ->
                        estimateTowardIndoor(outdoorTemperature = temperature, influence = 0.70)
                    },
                humidityPercent = outdoor.humidityPercent,
                source = CareEnvironmentSource.EstimatedIndoor,
            )
    }

/**
 * Linearly interpolates outdoor temperature toward a standard indoor baseline (21°C) based on insulation influence.
 */
private fun estimateTowardIndoor(
    outdoorTemperature: Double,
    influence: Double,
): Double {
    val assumedIndoorTemperature = 21.0
    val estimated = assumedIndoorTemperature + (outdoorTemperature - assumedIndoorTemperature) * influence
    return estimated.coerceIn(
        minimumValue = 15.0,
        maximumValue = 32.0,
    )
}

/**
 * Linearly interpolates outdoor humidity toward a standard indoor baseline (50%) based on insulation influence.
 */
private fun estimateHumidityTowardIndoor(
    outdoorHumidity: Int,
    influence: Double,
): Int {
    val assumedIndoorHumidity = 50
    return (assumedIndoorHumidity + (outdoorHumidity - assumedIndoorHumidity) * influence).toInt()
}
