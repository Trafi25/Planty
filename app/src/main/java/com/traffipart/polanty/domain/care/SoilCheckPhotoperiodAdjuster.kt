package com.traffipart.polanty.domain.care

import com.traffipart.polanty.domain.model.knowledge.WateringProfile
import javax.inject.Inject

/**
 * Adjusts soil check intervals based on seasonal daylight duration (photoperiod).
 *
 * During short winter days (<= 9 hours daylight), plants transpire slower, extending soil check intervals by 1 day.
 * During long summer days (>= 14 hours daylight), plants transpire faster, shortening soil check intervals by 1 day.
 * Output intervals are strictly constrained to the species's defined minimum and maximum check ranges.
 */
class SoilCheckPhotoperiodAdjuster
    @Inject
    constructor() {
        /**
         * Adjusts the base soil check interval days based on daylight hours and species limits.
         *
         * @param baseDays Base calculated check interval in days.
         * @param watering Botanical watering profile providing min and max allowed check intervals.
         * @param daylightHours Current daylight duration in hours, or `null` if unavailable.
         * @return Adjusted check interval in days, constrained to species limits.
         * @throws IllegalArgumentException If daylight hours is provided outside [0.0, 24.0].
         */
        fun adjustDays(
            baseDays: Int,
            watering: WateringProfile,
            daylightHours: Double?,
        ): Int {
            if (daylightHours == null) {
                return baseDays
            }

            require(daylightHours in 0.0..24.0) {
                "Daylight duration must be between 0 and 24 hours"
            }

            val adjustedDays =
                when {
                    daylightHours <= SHORT_DAY_HOURS -> {
                        baseDays + 1
                    }

                    daylightHours >= LONG_DAY_HOURS -> {
                        baseDays - 1
                    }

                    else -> {
                        baseDays
                    }
                }

            return adjustedDays.coerceIn(
                watering.soilCheckIntervalDaysMin,
                watering.soilCheckIntervalDaysMax,
            )
        }

        private companion object {
            const val SHORT_DAY_HOURS = 9.0
            const val LONG_DAY_HOURS = 14.0
        }
    }
