package com.traffipart.polanty.domain.model.care

/**
 * Represents ambient environmental conditions for care calculations.
 *
 * @property temperatureCelsius Ambient temperature in degrees Celsius, if available.
 * @property humidityPercent Relative humidity percentage, if available.
 */
data class CareEnvironment(
    val temperatureCelsius: Double?,
    val humidityPercent: Int?,
)
