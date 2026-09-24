package com.traffipart.polanty.domain.model

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
 */
enum class PlantSpaceType(
    val displayName: String,
) {
    LivingRoom("Living room"),
    Bedroom("Bedroom"),
    Bathroom("Bathroom"),
    Backyard("Backyard"),
    Kitchen("Kitchen"),
    Balcony("Balcony"),
    Office("Office"),
    Custom("Custom"),
}
