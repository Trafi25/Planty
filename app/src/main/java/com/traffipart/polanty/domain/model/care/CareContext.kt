package com.traffipart.polanty.domain.model.care

/**
 * Contextual environmental and astronomical conditions evaluated for plant care calculations.
 *
 * @property environment Estimated or observed room/outdoor microclimate conditions (temperature, humidity).
 * @property daylightHours Calculated duration of daylight in hours for the garden location and date.
 */
data class CareContext(
    val environment: CareEnvironment? = null,
    val daylightHours: Double? = null,
)
