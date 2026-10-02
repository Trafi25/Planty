package com.traffipart.polanty.domain.care

import com.traffipart.polanty.domain.model.care.CareEnvironment
import com.traffipart.polanty.domain.model.knowledge.HumidityRange
import com.traffipart.polanty.domain.model.knowledge.TemperatureRange
import com.traffipart.polanty.domain.model.knowledge.WateringProfile
import javax.inject.Inject

/**
 * Calculator that determines the optimal number of days between soil moisture checks.
 *
 * It evaluates a species's baseline [WateringProfile], preferred temperature and humidity ranges,
 * and current ambient [CareEnvironment] conditions to adjust watering intervals dynamically:
 * - **Hotter or Drier Conditions:** Uses the minimum check interval ([WateringProfile.soilCheckIntervalDaysMin]) because soil dries faster.
 * - **Cooler or More Humid Conditions:** Uses the maximum check interval ([WateringProfile.soilCheckIntervalDaysMax]) because soil retains moisture longer.
 * - **Ideal or Neutral Conditions:** Uses the midpoint interval between minimum and maximum bounds.
 */
class SoilCheckIntervalCalculator
    @Inject
    constructor() {
        /**
         * Calculates the target interval in days before the next soil check should occur.
         *
         * @param watering The species [WateringProfile] providing min and max check day bounds.
         * @param humidityRange The optimal species [HumidityRange], if available.
         * @param temperatureRange The optimal species [TemperatureRange], if available.
         * @param environment Current ambient environmental conditions ([CareEnvironment]), if available.
         * @return The calculated check interval in days.
         * @throws IllegalArgumentException If minimum interval is not positive or maximum interval is less than minimum.
         */
        fun calculateDays(
            watering: WateringProfile,
            humidityRange: HumidityRange?,
            temperatureRange: TemperatureRange?,
            environment: CareEnvironment?,
        ): Int {
            val minDays = watering.soilCheckIntervalDaysMin
            val maxDays = watering.soilCheckIntervalDaysMax
            require(minDays > 0) {
                "Minimum soil-check interval must be positive"
            }
            require(maxDays >= minDays) {
                "Maximum soil-check interval must not be below minimum"
            }
            if (environment == null) return minDays
            val hotterThanPreferred =
                environment.temperatureCelsius != null &&
                    temperatureRange != null &&
                    environment.temperatureCelsius > temperatureRange.maxCelsius

            val drierThanPreferred =
                environment.humidityPercent != null &&
                    humidityRange != null &&
                    environment.humidityPercent < humidityRange.minPercent

            if (hotterThanPreferred || drierThanPreferred) return minDays
            val coolerThanPreferred =
                environment.temperatureCelsius != null &&
                    temperatureRange != null &&
                    environment.temperatureCelsius < temperatureRange.minCelsius
            val moreHumidThanPreferred =
                environment.humidityPercent != null &&
                    humidityRange != null &&
                    environment.humidityPercent > humidityRange.maxPercent
            if (coolerThanPreferred || moreHumidThanPreferred) return maxDays
            return minDays + (maxDays - minDays) / 2
        }
    }
