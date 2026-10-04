package com.traffipart.polanty.domain.model.care

/**
 * Represents ambient environmental conditions for care calculations.
 *
 * @property temperatureCelsius Ambient temperature in degrees Celsius, if available.
 * @property humidityPercent Relative humidity percentage, if available.
 * @property source The origin or estimation method of the environmental data.
 */
data class CareEnvironment(
    val temperatureCelsius: Double?,
    val humidityPercent: Int?,
    val source: CareEnvironmentSource,
)

/**
 * Origin categories for environmental condition measurements.
 */
enum class CareEnvironmentSource {
    /** Live outdoor weather data retrieved from an external weather service. */
    OutdoorWeather,

    /** Indoor environmental conditions estimated from outdoor weather and room exposure. */
    EstimatedIndoor,

    /** Physical sensor readings connected to the device or local network. */
    Sensor,

    /** Manually specified user values. */
    Manual,
}
