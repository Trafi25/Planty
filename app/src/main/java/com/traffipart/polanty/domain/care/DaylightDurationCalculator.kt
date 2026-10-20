package com.traffipart.polanty.domain.care

import java.time.Instant
import java.time.ZoneOffset
import javax.inject.Inject
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.sin
import kotlin.math.tan

/**
 * Calculates daylight duration (photoperiod) in hours for a given geographic latitude and calendar date.
 *
 * Uses solar declination and sunrise/sunset hour angle equations to determine astronomical daylight length.
 */
class DaylightDurationCalculator
    @Inject
    constructor() {
        /**
         * Calculates photoperiod length in hours for the specified latitude and date timestamp.
         *
         * @param latitude Geographic latitude in degrees (-90.0 to 90.0).
         * @param atMillis Target date timestamp in milliseconds.
         * @return Calculated daylight duration in hours, constrained to [0.0, 24.0].
         * @throws IllegalArgumentException If latitude is outside [-90.0, 90.0].
         */
        fun calculateHours(
            latitude: Double,
            atMillis: Long,
        ): Double {
            require(latitude in -90.0..90.0) {
                "Latitude must be between -90 and 90"
            }

            val date =
                Instant
                    .ofEpochMilli(atMillis)
                    .atZone(ZoneOffset.UTC)
                    .toLocalDate()

            val dayOfYear = date.dayOfYear

            val daysInYear = date.lengthOfYear().toDouble()

            val declinationDegrees = 23.44 * sin(2.0 * PI * (284.0 + dayOfYear) / daysInYear)

            val latitudeRadians = latitude * PI / 180.0

            val declinationRadians = declinationDegrees * PI / 180.0

            val cosHourAngle = (-tan(latitudeRadians) * tan(declinationRadians)).coerceIn(-1.0, 1.0)

            val hourAngle = acos(cosHourAngle)

            return (24.0 / hourAngle).coerceIn(0.0, 24.0)
        }
    }
