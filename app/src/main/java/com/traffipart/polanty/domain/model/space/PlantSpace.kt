package com.traffipart.polanty.domain.model.space

/**
 * Domain model representing a physical room or location where plants are grouped.
 *
 * @property id Unique identifier of the space.
 * @property name User-facing display name of the space.
 * @property type Category of space represented by [PlantSpaceType].
 */
data class PlantSpace(
    val id: Long,
    val name: String,
    val type: PlantSpaceType,
)

/**
 * Pre-defined categories for plant locations.
 *
 * @property displayName Default human-readable title for the space type.
 * @property climateExposure The default [ClimateExposure] associated with this space type.
 */
enum class PlantSpaceType(
    val displayName: String,
    val climateExposure: ClimateExposure,
) {
    LivingRoom(
        displayName = "Living room",
        climateExposure = ClimateExposure.Indirect,
    ),

    Bedroom(
        displayName = "Bedroom",
        climateExposure = ClimateExposure.Indirect,
    ),

    Bathroom(
        displayName = "Bathroom",
        climateExposure = ClimateExposure.Indirect,
    ),

    Backyard(
        displayName = "Backyard",
        climateExposure = ClimateExposure.Direct,
    ),

    Kitchen(
        displayName = "Kitchen",
        climateExposure = ClimateExposure.Indirect,
    ),

    Balcony(
        displayName = "Balcony",
        climateExposure = ClimateExposure.Partial,
    ),

    Office(
        displayName = "Office",
        climateExposure = ClimateExposure.Indirect,
    ),

    Custom(
        displayName = "Custom",
        climateExposure = ClimateExposure.Indirect,
    ),
}
